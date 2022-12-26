package com.bkahlert.hello.clickup.ui.widgets

import com.bkahlert.Brand
import com.bkahlert.Brand.colors
import com.bkahlert.hello.clickup.api.FolderPreview
import com.bkahlert.hello.clickup.api.Identifier
import com.bkahlert.hello.clickup.api.Space
import com.bkahlert.hello.clickup.api.Tag
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.api.TaskID
import com.bkahlert.hello.clickup.api.TaskList
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.clickup.api.TaskListPreview
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.api.TimeEntryID
import com.bkahlert.hello.clickup.ui.widgets.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.ui.widgets.Activity.TaskActivity
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.compareTo
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.toMomentString
import kotlin.js.Date

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
        fun of(folderPreview: FolderPreview?): Meta? =
            folderPreview?.takeUnless { it.hidden }?.let { Meta("folder", "folder", text = it.name) }

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
    val listId: TaskListID?,
    val name: List<Meta>,
    val color: Color?,
    val tasks: List<Activity<*>>,
) {
    companion object {
        fun of(
            runningTaskActivity: RunningTaskActivity,
        ): ActivityGroup = ActivityGroup(
            null,
            listOf(Meta("running timer", "stop", "circle", text = "Running")),
            colors.red,
            listOf(runningTaskActivity),
        )

        fun of(
            timeEntry: TimeEntry,
            task: Task?,
        ): ActivityGroup = of(RunningTaskActivity(timeEntry, task))

        fun of(
            listId: TaskListID?,
            tasks: List<Task>,
            color: Color?,
            vararg meta: Meta?,
        ): ActivityGroup = ActivityGroup(
            listId = listId,
            name = listOfNotNull(*meta),
            color = color,
            tasks = tasks.map { task -> TaskActivity(task) },
        )

        fun of(
            space: Space?,
            folder: FolderPreview?,
            list: TaskList?,
            color: Color?,
            tasks: List<Task>,
        ): ActivityGroup = of(list?.id, tasks, color, Meta.of(space), Meta.of(folder), Meta.of(list))

        fun of(
            space: Space?,
            folder: FolderPreview?,
            listPreview: TaskListPreview?,
            color: Color?,
            tasks: List<Task>,
        ): ActivityGroup = of(listPreview?.id, tasks, color, Meta.of(space), Meta.of(folder), Meta.of(listPreview))
    }
}

val Iterable<ActivityGroup>.activities: List<Activity<*>> get() = flatMap { it.tasks }
fun Iterable<ActivityGroup>.byIdOrNull(id: Identifier<*>): Activity<*>? = firstNotNullOfOrNull { it.tasks.byIdOrNull(id) }
fun Iterable<ActivityGroup>.byId(id: Identifier<*>): Activity<*> = firstNotNullOf { it.tasks.byId(id) }
fun Iterable<Activity<*>>.byIdOrNull(id: Identifier<*>): Activity<*>? = firstOrNull { it.id == id }
fun Iterable<Activity<*>>.byId(id: Identifier<*>): Activity<*> = first { it.id == id }

/**
 * Visual presentation of a [Task] ([TaskActivity]) or a [TimeEntry] ([RunningTaskActivity]).
 */
sealed interface Activity<ID : Identifier<*>> {
    val id: ID
    val task: Task?
    val name: String
    val color: Color?
    val url: URL?
    val meta: List<Meta>
    val descriptions: Map<String, String?>
    val tags: Set<Tag>

    data class RunningTaskActivity(
        val timeEntry: TimeEntry,
        override val task: Task? = null,
    ) : Activity<TimeEntryID> {
        private val taskActivity: TaskActivity? = task?.let(::TaskActivity)
        override val id: TimeEntryID get() = timeEntry.id
        override val name: String
            get() = timeEntry.task?.name ?: taskActivity?.name ?: "— Timer with no associated task —"
        override val color: Color?
            get() = timeEntry.task?.status?.color ?: taskActivity?.color ?: Brand.colors.white.fade(1.0)
        override val url: URL? get() = timeEntry.url ?: timeEntry.url ?: taskActivity?.url
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
        override val task: Task,
    ) : Activity<TaskID> {
        override val id: TaskID get() = task.id
        override val name: String get() = task.name
        override val color: Color get() = task.status.color
        override val url: URL? get() = task.url
        override val meta: List<Meta>
            get() = buildList {
                if (task.dateCreated != null) {
                    add(Meta("created", "calendar", "alternate", "outline", text = task.dateCreated.toMomentString(true)))
                }
                when (task.dueDate?.compareTo(Date())) {
                    -1 -> add(Meta("due", "red", "calendar", "times", "outline", text = task.dueDate.toMomentString(false)))
                    +1 -> add(Meta("due", "calendar", "outline", text = task.dueDate.toMomentString(false)))
                    0 -> add(Meta("due", "yellow", "calendar", "outline", text = task.dueDate.toMomentString(false)))
                    else -> {}
                }
                if (task.timeEstimate != null) {
                    add(Meta("estimated time", "hourglass", "outline", text = task.timeEstimate.toMomentString(false)))
                }
                if (task.timeSpent != null) {
                    when (task.timeEstimate?.compareTo(task.timeSpent)) {
                        -1 -> add(
                            Meta(
                                "spent time (critical)",
                                "red",
                                "stopwatch",
                                text = task.timeSpent.toMomentString(false)
                            )
                        )

                        else -> add(Meta("spent time", "stopwatch", text = task.timeSpent.toMomentString(false)))
                    }
                }
            }
        override val descriptions: Map<String, String?> get() = mapOf("Task" to task.description?.takeUnless { it.isBlank() })
        override val tags: Set<Tag> get() = task.tags.toSet()
    }
}
