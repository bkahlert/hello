package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disabled
import com.bkahlert.hello.plugins.clickup.ClickUpMenuState.Transitioned.Succeeded.Disconnected
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.Spaces
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.TaskListBuilder
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.Teams
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.timeEntry
import com.bkahlert.hello.ui.demo.clickup.ClickUpTestClient
import com.bkahlert.hello.ui.demo.clickup.rememberClickUpMenuTestViewModel
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.appendJQuery
import com.bkahlert.kommons.appendSemanticUI
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.bkahlert.kommons.time.seconds
import com.clickup.api.FolderID
import com.clickup.api.FolderPreview
import com.clickup.api.Space
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.TaskListStatus
import com.clickup.api.TaskPreview
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import io.kotest.matchers.collections.contain
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.testutils.TestScope
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ClickUpMenuTest {

    @Test
    fun disabled_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disabled })
        }

        shouldRender("""
            <div>
                <div class="ui mini dimmable fluid one item menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui active inverted dimmer"></div>
                    <div class="link item"><img class="mini" src="clickup-icon.svg" alt="ClickUp">Connect to ClickUp</div>
                </div>
            </div>
        """.trimIndent())
    }

    @Test
    fun disconnected_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel { Disconnected })
        }
        waitForRecompositionComplete()

        shouldRender("""
            <div>
                <div class="ui mini dimmable fluid one item menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="link item"><img class="mini" src="clickup-icon.svg" alt="ClickUp">Connect to ClickUp</div>
                </div>
            </div>
        """.trimIndent())
    }

    @Test
    fun team_selecting_menu_with_multiple_teams() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = Teams.subList(0, 2))) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender("""
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
        """.trimIndent())
    }

    @Test
    fun team_selecting_menu_with_single_team() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = Teams.subList(0, 1))) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender("""
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
        """.trimIndent())
    }

    @Test
    fun team_selecting_menu_with_no_teams() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(ClickUpTestClient(initialTeams = emptyList())) { toTeamSelecting() })
        }
        waitForRecompositionComplete()

        shouldRender("""
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
        """.trimIndent())
    }


    @Test
    fun partially_loaded_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient()) { toPartiallyLoaded() })
        }
        waitForRecompositionComplete()

        shouldRender("""
            <div>
                <div class="ui mini dimmable menu">
                    <div class="ui inverted dimmer">
                        <div class="ui mini loader"></div>
                    </div>
                    <div class="ui borderless item dropdown" tabindex="0">
                        <img class="rounded avatar" src="${ClickUpFixtures.User.profilePicture}" alt="User john.doe"><i class="dropdown icon"></i>
                        <div class="menu" tabindex="-1">
                            <div class="ui item dropdown" tabindex="0"><i class="dropdown icon"></i><span class="text">Switch Team</span>
                                <div class="menu" tabindex="-1">
                                    <div class="disabled active link item">
                                        <div class="ui image" style="margin-left: -5px; margin-right: 2px;"><img class="ui avatar image" style="border-radius: 0em;" src="${Teams[0].avatar}" alt="Team Pear"></div><span style="padding-right: 3em;">Pear</span></div>
                                        <div class="link item"><div class="ui image" style="margin-left: -5px; margin-right: 2px;"><img class="ui avatar image" style="border-radius: 0em;" src="${Teams[1].avatar}" alt="Team Kommons"></div><span style="padding-right: 3em;">Kommons</span></div>
                                    </div>
                                </div>
                                <div class="link item"><i class="sync icon"></i>Refresh</div>
                                <div class="link item"><i class="sign-out icon"></i>Sign-out</div>
                            </div>
                        </div>
                        <div class="borderless link item">
                            <div><i class="link red stop circle icon"></i></div>
                            <span style="font-weight: 700;">12:29</span>
                        </div>
                        <div class="borderless link item" style="flex: 1 1 0%; min-width: 0px;">
                            <i class="square icon" style="color: rgb(2, 188, 212);"></i>
                            <div class="ui inline scrolling dropdown" style="flex: 1 1 0%; min-width: 0px;" tabindex="0">
                                <div class="text" style="max-width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; line-height: 1.1em;">Task task-1</div>
                                <div class="menu" style="max-width: 200%;" tabindex="-1">
                                    <div class="ui search icon input"><input type="text" placeholder="Search tasks..."><i class="search icon"></i></div>
                                    <div class="divider"></div>
                                    <div class="header" style="color: rgb(194, 30, 115);"><i class="stop circle icon" title="running timer"></i>Running</div>
                                    <div class="item" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" data-text="Task task-1" data-value="TimeEntryID::time-entry-1" data-variation="mini" data-offset="0" data-position="left center" data-html="<div class=&quot;ui items&quot;><div class=&quot;item&quot;><div class=&quot;content&quot;><div class=&quot;meta&quot;><span title=&quot;created&quot;><i class=&quot;calendar alternate outline icon&quot;></i> 3d 0h</span><span title=&quot;estimated time&quot;><i class=&quot;hourglass outline icon&quot;></i> 12h</span></div><div class=&quot;description&quot;><div class=&quot;ui list&quot;><div class=&quot;item&quot;><div class=&quot;sub header&quot;>Timer</div><div class=&quot;content&quot;>A time entry</div><div class=&quot;item&quot;><div class=&quot;sub header&quot;>Task</div><div class=&quot;content&quot;>Description of task task-1</div></div></div><div class=&quot;extra&quot;><span><i class=&quot;tag red icon&quot; style=&quot;color: hsl(9.13043478260871deg, 99.99999999999999%, 63.921568627450974%) !important&quot;></i> pomodoro-classic</span></div></div></div></div>">
                                        <i class="square icon" style="color: rgb(2, 188, 212);"></i>Task task-1</div>
                                    <div class="divider">
                                </div>
                                <div class="header"><i class="clone icon" title="project"></i>Space<i class="inverted icon"></i><i class="list icon" title="list"></i>Task List</div>
                                <div class="item" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" data-text="Task task-1" data-value="TaskID::task-1" data-variation="mini" data-offset="0" data-position="left center" data-html="<div class=&quot;ui items&quot;><div class=&quot;item&quot;><div class=&quot;content&quot;><div class=&quot;meta&quot;><span title=&quot;created&quot;><i class=&quot;calendar alternate outline icon&quot;></i> 3d 0h</span><span title=&quot;estimated time&quot;><i class=&quot;hourglass outline icon&quot;></i> 12h</span></div><div class=&quot;description&quot;>Description of task task-1</div></div></div></div>"><i class="square icon" style="color: rgb(2, 188, 212);"></i>Task task-1</div>
                                <div class="item" style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" data-text="Task task-2" data-value="TaskID::task-2" data-variation="mini" data-offset="0" data-position="left center" data-html="<div class=&quot;ui items&quot;><div class=&quot;item&quot;><div class=&quot;content&quot;><div class=&quot;meta&quot;><span title=&quot;created&quot;><i class=&quot;calendar alternate outline icon&quot;></i> 3d 0h</span><span title=&quot;estimated time&quot;><i class=&quot;hourglass outline icon&quot;></i> 12h</span></div><div class=&quot;description&quot;>Description of task task-2</div></div></div></div>"><i class="square icon" style="color: rgb(2, 188, 212);"></i>Task task-2</div></div></div></div><div class="right menu"><div class="borderless link item" style="padding-left: 0.5em; cursor: default; background-color: transparent;"><i class="hourglass outline icon" title="estimated time"></i>12h</div><div class="borderless link item" style="padding-left: 0.5em; cursor: default; background-color: transparent;"><i class="calendar alternate outline icon" title="created"></i>3d 0h</div><a class="item" style="padding-right: 0.6em;" href="https://app.clickup.com/t/task-1" target="_blank"><i class="external alternate icon" title="Open on ClickUp"></i></a>
                    </div>
                </div>
            </div>
        """.trimIndent())
    }

    @Test
    fun fully_loaded_menu() = runTest {
        root.appendJQuery()
        root.appendSemanticUI()
        composition {
            ClickUpMenu(rememberClickUpMenuTestViewModel(testClient()) { toFullyLoaded() })
        }
        waitForRecompositionComplete()

        shouldRender("""
            <div class="header" style="color: rgb(255, 0, 0);">
                <i class="clone icon" title="project"></i>Space<i class="inverted icon"></i>
                <i class="list icon" title="list"></i>Task List</div>
        """.trimIndent())
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
            it?.start should { start -> (Now - start!!) < 1.seconds }
            it?.end shouldBe null
            it?.tags!! shouldContain Pomodoro.Type.Default.tag
        }
    }

    @Test
    fun stop_pomodoro() = runTest {
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
            it?.start should { start -> (Now - start!!) < 1.seconds }
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
    root.innerHTML shouldContain expected.replace(">\\s+?<".toRegex(), "><")
}

private fun testClient(
    space: Space = ClickUpFixtures.space("1", "Space"),
    taskList: TaskList = TaskListBuilder.build {
        this.space = space
        name = "Task List"
        taskCount = 2
        status = TaskListStatus("private", Color.RGB(0xff0000))
    },
    folder: FolderPreview = FolderPreview(FolderID("folder-id"), name = "Hidden", hidden = true, access = true),
    vararg tasks: Task = arrayOf(
        ClickUpFixtures.task(id = "task-1", space = space.asPreview(), folder = folder, list = taskList.asPreview()),
        ClickUpFixtures.task(id = "task-2", space = space.asPreview(), folder = folder, list = taskList.asPreview()),
    ),
    runningTimeEntry: TimeEntry? = ClickUpFixtures.timeEntry(
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
