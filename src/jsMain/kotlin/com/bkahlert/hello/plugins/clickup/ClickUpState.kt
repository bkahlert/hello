package com.bkahlert.hello.plugins.clickup

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.kommons.fix.combine
import com.clickup.api.Folder
import com.clickup.api.FolderID
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Task
import com.clickup.api.TaskList
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.AccessToken

/**
 * State of a [ClickUpMenu]
 */
sealed class ClickUpState {
    /** Menu is not doing anything in order to save resources. */
    object Paused : ClickUpState()

    /** Menu is not connected to ClickUp. */
    object Disconnected : ClickUpState()

    /** Connection to ClickUp is established. */
    sealed class Connected(
        open val user: User,
        open val teams: List<Team>,
    ) : ClickUpState() {

        protected val logger = simpleLogger()

        /** A [Team] to work with needs to be selected. */
        data class TeamSelecting(
            override val user: User,
            override val teams: List<Team>,
        ) : Connected(user, teams)

        /** A [Team] to work with is selected. */
        data class TeamSelected(
            override val user: User,
            override val teams: List<Team>,
            val selectedTeam: Team,
            val selected: Selection,
            val runningTimeEntry: Result<TimeEntry?>?,
            val tasks: Result<List<Task>>? = null,
            val spaces: Result<List<Space>>? = null,
            val folders: Map<SpaceID, Result<List<Folder>>> = emptyMap(),
            val spaceLists: Map<SpaceID, Result<List<TaskList>>> = emptyMap(),
            val folderLists: Map<FolderID, Result<List<TaskList>>> = emptyMap(),
        ) : Connected(user, teams) {

            val runningActivity: Result<RunningTaskActivity?>? by lazy {
                runningTimeEntry?.map { timeEntry ->
                    if (timeEntry != null) {
                        val task = timeEntry.task?.let { timeEntryTask ->
                            tasks?.map { it.firstOrNull { task -> task.id == timeEntryTask.id } }?.getOrNull()
                        }
                        RunningTaskActivity(
                            timeEntry = timeEntry,
                            selected = listOfNotNull(timeEntry.id, task?.id).any { selected.contains(it) },
                            task = task
                        )
                    } else null
                }
            }

            val activityGroups: Result<List<ActivityGroup>>? by lazy {

                runningActivity.let { runningActivityResult ->
                    if (runningActivityResult == null) return@lazy null
                    runningActivityResult.mapCatching { runningActivity ->
                        buildList {
                            if (runningActivity != null) add(ActivityGroup.of(runningActivity))
                            if (tasks != null && spaces != null) {
                                combine(tasks, spaces) { tasks, spaces ->
                                    tasks.groupBy { it.space.id }
                                        .forEach { (spaceID, spaceTasks) ->
                                            val space = spaces.firstOrNull { it.id == spaceID }
                                            spaceTasks.groupBy { it.folder }
                                                .forEach { (folderPreview, folderTasks) ->
                                                    folderTasks.groupBy { it.list }
                                                        .forEach { (listPreview, listTasks) ->
                                                            val lists = when {
                                                                folderPreview.hidden -> spaceLists[spaceID]
                                                                else -> folderLists[folderPreview.id]
                                                            }?.getOrThrow() ?: emptyList()

                                                            val group = lists.firstOrNull { it.id == listPreview?.id }?.let { taskList ->
                                                                ActivityGroup.of(
                                                                    space = space,
                                                                    folder = folderPreview,
                                                                    list = taskList,
                                                                    color = taskList.status?.color,
                                                                    tasks = listTasks,
                                                                    selected = this@TeamSelected.selected,
                                                                )
                                                            } ?: ActivityGroup.of(
                                                                space = space,
                                                                folder = folderPreview,
                                                                listPreview = listPreview,
                                                                color = null,
                                                                tasks = listTasks,
                                                                selected = this@TeamSelected.selected,
                                                            )
                                                            add(group)
                                                        }
                                                }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data class Failed(
        val accessToken: AccessToken,
        val exception: Throwable,
    ) : ClickUpState() {
        val message: String = exception.message ?: "message missing"
    }

    override fun toString(): String {
        return this::class.simpleName ?: "<object>"
    }
}
