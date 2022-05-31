package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.plugins.clickup.menu.Activity
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityDropdown
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.hello.plugins.clickup.menu.activities
import com.bkahlert.hello.plugins.clickup.menu.rememberActivityDropdownState
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.clickup.ActivityDropdownFixtures.select
import com.bkahlert.kommons.color.Color
import com.clickup.api.FolderPreview
import com.clickup.api.Space
import com.clickup.api.Task
import com.clickup.api.TaskList
import com.clickup.api.TimeEntry

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
