package com.bkahlert.hello.plugins.clickup

import com.bkahlert.Brand.colors
import com.bkahlert.hello.plugins.clickup.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.Activity.TaskActivity
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.compareTo
import com.bkahlert.kommons.time.toMoment
import com.clickup.api.FolderPreview
import com.clickup.api.Identifier
import com.clickup.api.Space
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.TaskListPreview
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import io.ktor.http.Url

/**
 * Some king of icon like meta information
 * consisting of Semantic UI [iconVariations],
 * a [title] and an optional [text].
 */
data class Meta(
    val iconVariations: List<String>,
    val title: String,
    val text: String?,
) {
    constructor(
        title: String,
        vararg iconVariations: String,
        text: String? = null,
    ) : this(iconVariations.asList(), title = title, text = text)

    companion object {
        /** A meta describing the specified [space]. */
        fun of(space: Space?): Meta = Meta("project", "clone", text = space?.name ?: "[no space name]")

        /** A meta describing the specified [folderPreview]. */
        fun of(folderPreview: FolderPreview?): Meta? = folderPreview?.takeUnless { it.hidden }?.let { Meta("folder", "folder", text = it.name) }

        /** A meta describing the specified [list]. */
        fun of(list: TaskList?): Meta? = list?.let { Meta("list", "list", text = it.name) }

        /** A meta describing the specified [list]. */
        fun of(list: TaskListPreview?): Meta = Meta("list", "list", text = list?.name ?: "[no list name]")
    }
}

/**
 * A group of [Activity] instances with a [name] (composed using a list of [Meta]),
 * and an optional [color].
 */
data class ActivityGroup(
    val name: List<Meta>,
    val color: Color?,
    val tasks: List<Activity<*>>,
) {
    companion object {
        fun of(
            runningTaskActivity: RunningTaskActivity,
        ): ActivityGroup = ActivityGroup(
            listOf(Meta("running timer", "stop", "circle", text = "Running")),
            colors.red,
            listOf(runningTaskActivity),
        )

        fun of(
            timeEntry: TimeEntry,
            selected: Boolean,
            task: Task?,
        ): ActivityGroup = of(RunningTaskActivity(timeEntry, selected, task))

        fun of(
            tasks: List<Task>,
            selected: Selection,
            color: Color?,
            vararg meta: Meta?,
        ): ActivityGroup = ActivityGroup(
            name = listOfNotNull(*meta),
            color = color,
            tasks = tasks.map { task -> TaskActivity(task, selected.contains(task.id)) },
        )

        fun of(
            space: Space?,
            folder: FolderPreview?,
            list: TaskList?,
            color: Color?,
            tasks: List<Task>,
            selected: Selection,
        ): ActivityGroup = of(tasks, selected, color, Meta.of(space), Meta.of(folder), Meta.of(list))

        fun of(
            space: Space?,
            folder: FolderPreview?,
            listPreview: TaskListPreview?,
            color: Color?,
            tasks: List<Task>,
            selected: Selection,
        ): ActivityGroup = of(tasks, selected, color, Meta.of(space), Meta.of(folder), Meta.of(listPreview))
    }
}

/** Contains the selected activities. */
val Iterable<ActivityGroup>.selected: List<Activity<*>>
    get() = flatMap { group -> group.tasks.filter { activity -> activity.selected } }

/** Contains the selected activities. */
fun Iterable<ActivityGroup>.filter(selected: Selection): List<Activity<*>> =
    flatMap { group -> group.tasks.filter { activity -> selected.contains(activity.id) } }


/**
 * Visual presentation of a [Task] ([TaskActivity]) or a [TimeEntry] ([RunningTaskActivity]).
 */
sealed interface Activity<ID : Identifier<*>> {
    val id: ID?
    val taskID: TaskID?
    val name: String
    val color: Color?
    val url: Url?
    val meta: List<Meta>
    val descriptions: Map<String, String?>
    val tags: Set<Tag>
    val selected: Boolean

    data class RunningTaskActivity(
        val timeEntry: TimeEntry,
        override val selected: Boolean = false,
        val task: Task? = null,
    ) : Activity<TimeEntryID> {
        private val taskActivity: TaskActivity? = task?.let(::TaskActivity)
        override val id: TimeEntryID get() = timeEntry.id
        override val taskID: TaskID? get() = task?.id
        override val name: String get() = timeEntry.task?.name ?: taskActivity?.name ?: "Timer running"
        override val color: Color? get() = timeEntry.task?.status?.color ?: taskActivity?.color
        override val url: Url? get() = timeEntry.url ?: timeEntry.taskUrl ?: taskActivity?.url
        override val meta: List<Meta>
            get() = buildList {
                if (timeEntry.billable) add(Meta("billable", "dollar"))
                taskActivity?.also { addAll(it.meta) }
            }
        override val descriptions: Map<String, String?>
            get() = buildMap {
                put("Timer", timeEntry.description.takeUnless { it.isBlank() })
                taskActivity?.also { putAll(it.descriptions) }
            }
        override val tags: Set<Tag>
            get() = buildSet {
                addAll(timeEntry.tags)
                taskActivity?.also { addAll(it.tags) }
            }
    }

    data class TaskActivity(
        val task: Task,
        override val selected: Boolean = false,
    ) : Activity<TaskID> {
        override val id: TaskID get() = task.id
        override val taskID: TaskID get() = task.id
        override val name: String get() = task.name
        override val color: Color get() = task.status.color
        override val url: Url? get() = task.url
        override val meta: List<Meta>
            get() = buildList {
                if (task.dateCreated != null) {
                    add(Meta("created", "calendar", "alternate", "outline", text = task.dateCreated.toMoment()))
                }
                when (task.dueDate?.compareTo(Now)) {
                    -1 -> add(Meta("due", "red", "calendar", "times", "outline", text = task.dueDate.toMoment(false)))
                    +1 -> add(Meta("due", "calendar", "outline", text = task.dueDate.toMoment(false)))
                    0 -> add(Meta("due", "yellow", "calendar", "outline", text = task.dueDate.toMoment(false)))
                    else -> {}
                }
                if (task.timeEstimate != null) {
                    add(Meta("estimated time", "hourglass", "outline", text = task.timeEstimate.toMoment(false)))
                }
                if (task.timeSpent != null) {
                    when (task.timeEstimate?.compareTo(task.timeSpent)) {
                        -1 -> add(Meta("spent time (critical)", "red", "stopwatch", text = task.timeSpent.toMoment(false)))
                        else -> add(Meta("spent time", "stopwatch", text = task.timeSpent.toMoment(false)))
                    }
                }
            }
        override val descriptions: Map<String, String?> get() = mapOf("Task" to task.description?.takeUnless { it.isBlank() })
        override val tags: Set<Tag> get() = task.tags.toSet()
    }
}
