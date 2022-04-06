package com.bkahlert.hello.ui.demo.clickup

import androidx.compose.runtime.Composable
import com.bkahlert.Brand
import com.bkahlert.hello.plugins.clickup.Selection
import com.bkahlert.hello.plugins.clickup.menu.Activity
import com.bkahlert.hello.plugins.clickup.menu.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.menu.Activity.TaskActivity
import com.bkahlert.hello.plugins.clickup.menu.ActivityDropdown
import com.bkahlert.hello.plugins.clickup.menu.ActivityGroup
import com.bkahlert.hello.ui.demo.Demo
import com.bkahlert.hello.ui.demo.Demos
import com.bkahlert.hello.ui.demo.clickup.ActivityDropdownFixtures.withSelection
import com.bkahlert.kommons.Color
import com.clickup.api.FolderPreview
import com.clickup.api.Space
import com.clickup.api.Task
import com.clickup.api.TaskList
import com.clickup.api.TimeEntry
import kotlin.random.Random

@Composable
fun ActivityDropdownDemo() {
    Demos("Activity Dropdown") {
        Demo("No selection") {
            ActivityDropdown(
                activityGroups = ActivityDropdownFixtures.ActivityGroups,
                onSelect = onSelect,
            )
        }
        Demo("Selection") {
            ActivityDropdown(
                activityGroups = ActivityDropdownFixtures.ActivityGroups.withSelection { index, _ -> index == 3 },
                onSelect = onSelect,
            )
        }
        Demo("Empty") {
            ActivityDropdown(
                activityGroups = emptyList(),
                onSelect = onSelect,
            )
        }
    }
}

object ActivityDropdownFixtures {
    fun runningTaskActivity(
        timeEntry: TimeEntry = ClickupFixtures.TimeEntry,
        task: Task? = ClickupFixtures.Tasks.first(),
    ) = RunningTaskActivity(
        timeEntry = timeEntry,
        task = task,
    )

    fun runningTaskActivityGroup(
        runningTaskActivity: RunningTaskActivity = runningTaskActivity(),
    ) = ActivityGroup.of(runningTaskActivity)

    fun taskActivityGroup(
        space: Space? = ClickupFixtures.Spaces.first(),
        folder: FolderPreview? = ClickupFixtures.Space1FolderLists.first().folder,
        list: TaskList? = ClickupFixtures.Space1FolderLists.first(),
        color: Color? = Brand.colors.primary.toHSL().run { copy(h = Random.nextDouble(0.0, 360.0)) },
        tasks: List<Task> = ClickupFixtures.Tasks,
        fromIndex: Int = 0,
        toIndex: Int = tasks.size,
        select: (Int, Task) -> Boolean = { _, _ -> false },
    ): ActivityGroup {
        val taskSubList = tasks.subList(fromIndex, toIndex)
        return ActivityGroup.of(space, folder, list, color, taskSubList, taskSubList.filterIndexed(select).map { it.id })
    }

    fun List<ActivityGroup>.withSelection(select: (Int, Activity<*>) -> Boolean) =
        map { group ->
            group.copy(tasks = group.tasks.mapIndexed { index, activity ->
                val selected = select(index, activity)
                when (activity) {
                    is RunningTaskActivity -> activity.copy(selected = selected)
                    is TaskActivity -> activity.copy(selected = selected)
                }
            })
        }

    val ActivityGroups: List<ActivityGroup> by lazy {
        listOf(
            taskActivityGroup(fromIndex = 0, toIndex = 5, space = null),
            taskActivityGroup(fromIndex = 5, toIndex = 14, folder = null, color = null),
            taskActivityGroup(fromIndex = 14, toIndex = 21, list = null),
        )
    }
    val ActivityGroupsWithRunningActivity: List<ActivityGroup> by lazy {
        buildList {
            add(runningTaskActivityGroup())
            addAll(ActivityGroups)
        }
    }
    val ActivityGroupsWithTaskLessRunningActivity: List<ActivityGroup> by lazy {
        buildList {
            add(runningTaskActivityGroup(runningTaskActivity(task = null)))
            addAll(ActivityGroups)
        }
    }
}

private val onSelect: (Selection) -> Unit = {
    console.info("selected $it")
}
