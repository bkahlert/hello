package com.bkahlert.hello.clickup.ui

import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.model.ClickUpFixtures
import com.bkahlert.hello.clickup.model.ClickUpFixtures.Spaces
import com.bkahlert.hello.clickup.model.ClickUpFixtures.TaskListBuilder
import com.bkahlert.hello.clickup.model.ClickUpFixtures.Teams
import com.bkahlert.hello.clickup.model.ClickUpFixtures.timeEntry
import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.FolderPreview
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListStatus
import com.bkahlert.hello.clickup.model.TaskPreview
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.clickup.ui.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.color.Color.RGB
import com.bkahlert.hello.debug.clickup.ClickUpTestClient
import com.bkahlert.hello.debug.clickup.rememberClickUpMenuTestViewModel
import com.bkahlert.kommons.appendJQuery
import com.bkahlert.kommons.appendSemanticUI
import com.bkahlert.kommons.minus
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.testutils.TestScope
import org.jetbrains.compose.web.testutils.runTest
import kotlin.js.Date
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class ClickUpMenuTest {

    @Test
    fun disabled_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disabled })
        }

        shouldRender(
            """
            <div>
                <div class="ui mini dimmable fluid one item menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui active inverted dimmer"></div>
                    <div class="link item"><img class="mini" src="clickup-icon.svg" alt="ClickUp">Connect to ClickUp</div>
                </div>
            </div>
        """.trimIndent()
        )
    }

    @Test
    fun disconnected_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disconnected })
        }
        waitForRecompositionComplete()

        shouldRender(
            """
            <div>
                <div class="ui mini dimmable fluid one item menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="link item"><img class="mini" src="clickup-icon.svg" alt="ClickUp">Connect to ClickUp</div>
                </div>
            </div>
        """.trimIndent()
        )
    }

    @Test
    fun team_selecting_menu_with_multiple_teams() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = Teams.subList(0, 2))) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender(
            """
            <div>
                <div class="ui mini dimmable menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui borderless item dropdown" tabindex="0">
                        <img class="rounded avatar" src="${ClickUpFixtures.User.profilePicture}" alt="User john.doe"><i class="dropdown icon"></i>
                        <div class="menu" tabindex="-1">
                            <div class="link item"><i class="sync icon"></i>Refresh</div>
                            <div class="link item"><i class="sign-out icon"></i>Sign-out</div>
                        </div>
                    </div>
                    <div class="borderless disabled link item"><span>Select team:</span></div>
                    <div class="borderless link item"><img class="avatar" src="${Teams[0].avatar}" alt="Team Pear"><span>Pear</span></div>
                    <div class="borderless link item"><img class="avatar" src="${Teams[1].avatar}" alt="Team Kommons"><span>Kommons</span></div>
                </div>
            </div>
        """.trimIndent()
        )
    }

    @Test
    fun team_selecting_menu_with_single_team() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = Teams.subList(0, 1))) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender(
            """
            <div>
                <div class="ui mini dimmable menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui borderless item dropdown" tabindex="0">
                        <img class="rounded avatar" src="${ClickUpFixtures.User.profilePicture}" alt="User john.doe"><i class="dropdown icon"></i>
                        <div class="menu" tabindex="-1">
                            <div class="link item"><i class="sync icon"></i>Refresh</div>
                            <div class="link item"><i class="sign-out icon"></i>Sign-out</div>
                        </div>
                    </div>
                    <div class="borderless disabled link item"><span>Select team:</span></div>
                    <div class="borderless link item"><img class="avatar" src="${Teams[0].avatar}" alt="Team Pear"><span>Pear</span></div>
                </div>
            </div>
        """.trimIndent()
        )
    }

    @Test
    fun team_selecting_menu_with_no_teams() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = emptyList())) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender(
            """
            <div>
                <div class="ui mini dimmable menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui borderless item dropdown" tabindex="0">
                        <img class="rounded avatar" src="${ClickUpFixtures.User.profilePicture}" alt="User john.doe"><i class="dropdown icon"></i>
                        <div class="menu" tabindex="-1">
                            <div class="link item"><i class="sync icon"></i>Refresh</div>
                            <div class="link item"><i class="sign-out icon"></i>Sign-out</div>
                        </div>
                    </div>
                    <div class="borderless disabled link item"><span>No teams found</span></div>
                </div>
            </div>
        """.trimIndent()
        )
    }


    @Test
    fun partially_loaded_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient()) { toPartiallyLoaded() })
        }
        waitForRecompositionComplete()

        shouldRenderPartially(">Task task-1<")
    }

    @Test
    fun fully_loaded_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient()) { toFullyLoaded() })
        }
        waitForRecompositionComplete()

        shouldRenderPartially(">Task task-1<")
    }

    @Test
    fun start_pomodoro() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        val testClient = testClient(runningTimeEntry = null)
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient) { toFullyLoaded() })
        }
        waitForRecompositionComplete()

        pressPlay()
        delay(.5.seconds)

        testClient.getRunningTimeEntry(Teams.first(), ClickUpFixtures.User) should {
            it?.task?.id?.stringValue shouldBe "task-1"
            it?.user shouldBe ClickUpFixtures.User
            it?.billable shouldBe false
            it?.start should { start -> (Date() - start!!) < 1.seconds }
            it?.end shouldBe null
            it?.tags!! shouldContain Pomodoro.Type.Default.tag
        }
    }

    @Test
    fun stop_pomodoro() = runTest {
        if (true) return@runTest // TODO only runs in isolation
        root.appendJQuery()
        root.appendSemanticUI()
        val testClient = testClient(
            runningTimeEntry = timeEntry("already-running", TaskPreview(id = TaskID("task-1"), "Task task-1", Spaces.first().statuses[1].asPreview(), null)),
        )
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient) { toFullyLoaded() })
        }
        waitForRecompositionComplete()

        pressStop()
        delay(.5.seconds)

        testClient.getRunningTimeEntry(Teams.first(), ClickUpFixtures.User) shouldBe null
        testClient.getTask(TaskID("task-1")) should {
            it?.tags!! should {
                contain(Pomodoro.Type.Default.tag)
                contain(Pomodoro.Status.Aborted.tag)
            }
        }
        testClient.getTimeEntry(Teams.first(), TimeEntryID("already-running")) should {
            it?.ended shouldBe true
            it?.tags!! should {
                contain(Pomodoro.Type.Default.tag)
                contain(Pomodoro.Status.Aborted.tag)
            }
        }
    }

    @Test
    fun start_pomodoro_despite_already_running_task() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        val testClient = testClient(
            runningTimeEntry = timeEntry("already-running", TaskPreview(id = TaskID("task-1"), "Task task-1", Spaces.first().statuses[1].asPreview(), null)),
        )
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient) { toFullyLoaded(select = { _, id -> id == TaskID("task-2") }) })
        }
        waitForRecompositionComplete()

        pressPlay()
        delay(.5.seconds)

        testClient.getRunningTimeEntry(Teams.first(), ClickUpFixtures.User) should {
            it?.task?.id?.stringValue shouldBe "task-2"
            it?.user shouldBe ClickUpFixtures.User
            it?.billable shouldBe false
            it?.start should { start -> (Date() - start!!) < 1.seconds }
            it?.end shouldBe null
            it?.tags!! shouldContain Pomodoro.Type.Default.tag
        }
        testClient.getTask(TaskID("task-1")) should {
            it?.tags!! should {
                contain(Pomodoro.Type.Default.tag)
                contain(Pomodoro.Status.Aborted.tag)
            }
        }
        testClient.getTimeEntry(Teams.first(), TimeEntryID("already-running")) should {
            it?.ended shouldBe true
            it?.tags!! should {
                contain(Pomodoro.Type.Default.tag)
                contain(Pomodoro.Status.Aborted.tag)
            }
        }
    }
}

infix fun TestScope.shouldRender(expected: String) {
    root.innerHTML shouldBe expected.replace(">\\s+?<".toRegex(), "><")
}

infix fun TestScope.shouldRenderPartially(expected: String) {
    root.innerHTML shouldContain expected.replace(">\\s+?<".toRegex(), "><")
}

private fun testClient(
    space: Space = ClickUpFixtures.space("1", "Space"),
    taskList: TaskList = TaskListBuilder.build {
        this.space = space
        name = "Task List"
        taskCount = 2
        status = TaskListStatus("private", RGB(0xff0000))
    },
    folder: FolderPreview = FolderPreview(FolderID("folder-id"), name = "Hidden", hidden = true, access = true),
    vararg tasks: Task = arrayOf(
        ClickUpFixtures.task(id = "task-1", space = space.asPreview(), folder = folder, list = taskList.asPreview()),
        ClickUpFixtures.task(id = "task-2", space = space.asPreview(), folder = folder, list = taskList.asPreview()),
    ),
    runningTimeEntry: TimeEntry? = timeEntry(
        id = "time-entry-1",
        task = tasks.firstOrNull()?.asPreview()
    ),
) = ClickUpTestClient(
    initialUser = ClickUpFixtures.User,
    initialTeams = Teams,
    initialTasks = tasks.toList(),
    initialSpaces = listOf(space),
    initialLists = listOf(taskList),
    initialFolders = emptyList(),
    initialTimeEntries = emptyList(),
    initialRunningTimeEntry = runningTimeEntry,
    delayFactor = .005,
)

private fun pressPlay() {
    js("jQuery('.green.play.icon').closest('.item').click()")
}

private fun pressStop() {
    js("jQuery('.red.stop.icon').closest('.item').click()")
}
