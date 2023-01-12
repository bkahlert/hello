package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.api.FolderPreview
import com.bkahlert.hello.clickup.api.Space
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.api.TaskList
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.ui.widgets.Activity
import com.bkahlert.hello.clickup.ui.widgets.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.ui.widgets.ActivityDropdown
import com.bkahlert.hello.clickup.ui.widgets.ActivityGroup
import com.bkahlert.hello.clickup.ui.widgets.activities
import com.bkahlert.hello.clickup.ui.widgets.rememberActivityDropdownState
import com.bkahlert.hello.color.Color
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.debug.clickup.ActivityDropdownFixtures.select

@Composable
fun ActivityDropdownDemo() {
    Demos("Activity Dropdown") {
        Demo("No selection") {
            ActivityDropdown(rememberActivityDropdownState(ActivityDropdownFixtures.ActivityGroups))
        }
        Demo("Selection") {
            ActivityDropdown(
                rememberActivityDropdownState(ActivityDropdownFixtures.ActivityGroups,
                    ActivityDropdownFixtures.ActivityGroups.select { index, _ -> index == 3 }),
            )
        }
        Demo("Empty") {
            ActivityDropdown(rememberActivityDropdownState())
        }
    }
}

object ActivityDropdownFixtures {
    fun runningTaskActivity(
        timeEntry: TimeEntry = ClickUpFixtures.TimeEntry,
        task: Task? = ClickUpFixtures.Tasks.first(),
    ) = RunningTaskActivity(
        timeEntry = timeEntry,
        task = task,
    )

    fun runningTaskActivityGroup(
        runningTaskActivity: RunningTaskActivity = runningTaskActivity(),
    ) = ActivityGroup.of(runningTaskActivity)

    fun taskActivityGroup(
        space: Space? = ClickUpFixtures.Spaces.first(),
        folder: FolderPreview? = ClickUpFixtures.Space1FolderLists.first().folder,
        list: TaskList? = ClickUpFixtures.Space1FolderLists.first(),
        color: Color? = Color.random(),
        tasks: List<Task> = ClickUpFixtures.Tasks,
        fromIndex: Int = 0,
        toIndex: Int = tasks.size,
    ): ActivityGroup {
        return ActivityGroup.of(space, folder, list, color, tasks.subList(fromIndex, toIndex))
    }

    fun List<ActivityGroup>.select(select: (Int, Activity<*>) -> Boolean) =
        activities.filterIndexed(select).firstOrNull()

    val ActivityGroups: List<ActivityGroup> = listOf(
        taskActivityGroup(fromIndex = 0, toIndex = 5, space = null),
        taskActivityGroup(fromIndex = 5, toIndex = 14, folder = null, color = null),
        taskActivityGroup(fromIndex = 14, toIndex = 21, list = null),
    )
    val ActivityGroupsWithRunningActivity: List<ActivityGroup> = buildList {
        add(runningTaskActivityGroup())
        addAll(ActivityGroups)
    }
    val ActivityGroupsWithTaskLessRunningActivity: List<ActivityGroup> = buildList {
        add(runningTaskActivityGroup(runningTaskActivity(task = null)))
        addAll(ActivityGroups)
    }
}
