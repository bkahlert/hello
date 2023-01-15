package com.bkahlert.hello.debug.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.FolderPreview
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.view.Activity
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.view.ActivityDropdown
import com.bkahlert.hello.clickup.view.ActivityGroup
import com.bkahlert.hello.clickup.view.activities
import com.bkahlert.hello.clickup.view.rememberActivityDropdownState
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
