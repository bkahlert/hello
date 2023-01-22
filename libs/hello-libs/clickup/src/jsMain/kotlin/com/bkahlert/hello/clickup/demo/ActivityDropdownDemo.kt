package com.bkahlert.hello.clickup.demo

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.demo.ActivityDropdownFixtures.select
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
import com.bkahlert.kommons.color.Color
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos

@Composable
public fun ActivityDropdownDemo() {
    Demos("Activity Dropdown") {
        Demo("No selection") {
            ActivityDropdown(rememberActivityDropdownState(ActivityDropdownFixtures.ActivityGroups))
        }
        Demo("Selection") {
            ActivityDropdown(
                rememberActivityDropdownState(
                    ActivityDropdownFixtures.ActivityGroups,
                    ActivityDropdownFixtures.ActivityGroups.select { index, _ -> index == 3 }),
            )
        }
        Demo("Empty") {
            ActivityDropdown(rememberActivityDropdownState())
        }
    }
}

// TODO move to viewmodel.fixtures
public object ActivityDropdownFixtures {

    public fun runningTaskActivity(
        timeEntry: TimeEntry = ClickUpFixtures.TimeEntry,
        task: Task? = ClickUpFixtures.Tasks.first(),
    ): RunningTaskActivity = RunningTaskActivity(
        timeEntry = timeEntry,
        task = task,
    )

    public fun runningTaskActivityGroup(
        runningTaskActivity: RunningTaskActivity = runningTaskActivity(),
    ): ActivityGroup = ActivityGroup.of(runningTaskActivity)

    public fun taskActivityGroup(
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

    public fun List<ActivityGroup>.select(select: (Int, Activity<*>) -> Boolean): Activity<*>? =
        activities.filterIndexed(select).firstOrNull()

    public val ActivityGroups: List<ActivityGroup> = listOf(
        taskActivityGroup(fromIndex = 0, toIndex = 5, space = null),
        taskActivityGroup(fromIndex = 5, toIndex = 14, folder = null, color = null),
        taskActivityGroup(fromIndex = 14, toIndex = 21, list = null),
    )
    public val ActivityGroupsWithRunningActivity: List<ActivityGroup> = buildList {
        add(runningTaskActivityGroup())
        addAll(ActivityGroups)
    }
    public val ActivityGroupsWithTaskLessRunningActivity: List<ActivityGroup> = buildList {
        add(runningTaskActivityGroup(runningTaskActivity(task = null)))
        addAll(ActivityGroups)
    }
}
