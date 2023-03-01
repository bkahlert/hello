package com.bkahlert.hello.clickup.view

import com.bkahlert.hello.clickup.model.FolderPreview
import com.bkahlert.hello.clickup.model.Identifier
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.TaskListPreview
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.view.Activity.RunningTaskActivity
import com.bkahlert.hello.clickup.view.Activity.TaskActivity
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Colors
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.toMomentString
import com.bkahlert.kommons.uri.Uri

/**
 * Some king of icon like meta information
 * consisting of Semantic UI [iconVariations],
 * a [title] and an optional [text].
 */
public data class Meta(
    val iconVariations: List<String>,
    val title: String,
    val text: String?,
) {
    public constructor(
        title: String,
        vararg iconVariations: String,
        text: String? = null,
    ) : this(iconVariations.asList(), title = title, text = text)

    public companion object {
        /** A meta describing the specified [space]. */
        public fun of(space: Space?): Meta = Meta("project", "clone", text = space?.name ?: "[no space name]")

        /** A meta describing the specified [folderPreview]. */
        public fun of(folderPreview: FolderPreview?): Meta? =
            folderPreview?.takeUnless { it.hidden }?.let { Meta("folder", "folder", text = it.name) }

        /** A meta describing the specified [list]. */
        public fun of(list: TaskList?): Meta? = list?.let { Meta("list", "list", text = it.name) }

        /** A meta describing the specified [list]. */
        public fun of(list: TaskListPreview?): Meta = Meta("list", "list", text = list?.name ?: "[no list name]")
    }
}

/**
 * A group of [Activity] instances with a [name] (composed using a list of [Meta]),
 * and an optional [color].
 */
public data class ActivityGroup(
    val listId: TaskListID?,
    val name: List<Meta>,
    val color: Color?,
    val tasks: List<Activity<*>>,
) {
    public companion object {
        public fun of(
            runningTaskActivity: RunningTaskActivity,
        ): ActivityGroup = ActivityGroup(
            null,
            listOf(Meta("running timer", "stop", "circle", text = "Running")),
            Colors.red,
            listOf(runningTaskActivity),
        )

        public fun of(
            timeEntry: TimeEntry,
            task: Task?,
        ): ActivityGroup = of(RunningTaskActivity(timeEntry, task))

        public fun of(
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

        public fun of(
            space: Space?,
            folder: FolderPreview?,
            list: TaskList?,
            color: Color?,
            tasks: List<Task>,
        ): ActivityGroup = of(list?.id, tasks, color, Meta.of(space), Meta.of(folder), Meta.of(list))

        public fun of(
            space: Space?,
            folder: FolderPreview?,
            listPreview: TaskListPreview?,
            color: Color?,
            tasks: List<Task>,
        ): ActivityGroup = of(listPreview?.id, tasks, color, Meta.of(space), Meta.of(folder), Meta.of(listPreview))
    }
}

public val Iterable<ActivityGroup>.activities: List<Activity<*>> get() = flatMap { it.tasks }
public fun Iterable<ActivityGroup>.byIdOrNull(id: Identifier<*>): Activity<*>? = firstNotNullOfOrNull { it.tasks.byIdOrNull(id) }
public fun Iterable<ActivityGroup>.byId(id: Identifier<*>): Activity<*> = firstNotNullOf { it.tasks.byId(id) }
public fun Iterable<Activity<*>>.byIdOrNull(id: Identifier<*>): Activity<*>? = firstOrNull { it.id == id }
public fun Iterable<Activity<*>>.byId(id: Identifier<*>): Activity<*> = first { it.id == id }

/**
 * Visual presentation of a [Task] ([TaskActivity]) or a [TimeEntry] ([RunningTaskActivity]).
 */
public sealed interface Activity<ID : Identifier<*>> {
    public val id: ID
    public val task: Task?
    public val name: String
    public val color: Color?
    public val url: Uri?
    public val meta: List<Meta>
    public val descriptions: Map<String, String?>
    public val tags: Set<Tag>

    public data class RunningTaskActivity(
        val timeEntry: TimeEntry,
        override val task: Task? = null,
    ) : Activity<TimeEntryID> {
        private val taskActivity: TaskActivity? = task?.let(::TaskActivity)
        override val id: TimeEntryID get() = timeEntry.id
        override val name: String
            get() = timeEntry.task?.name ?: taskActivity?.name ?: "— Timer with no associated task —"
        override val color: Color?
            get() = timeEntry.task?.status?.color ?: taskActivity?.color ?: Colors.white.fade(1.0)
        override val url: Uri? get() = timeEntry.url ?: timeEntry.url ?: taskActivity?.url
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

    public data class TaskActivity(
        override val task: Task,
    ) : Activity<TaskID> {
        override val id: TaskID get() = task.id
        override val name: String get() = task.name
        override val color: Color get() = task.status.color
        override val url: Uri? get() = task.url
        override val meta: List<Meta>
            get() = buildList {
                when (val dateCreated = task.dateCreated) {
                    null -> {}
                    else -> {
                        add(Meta("created", "calendar", "alternate", "outline", text = dateCreated.toMomentString(true)))
                    }
                }
                when (val dueDate = task.dueDate) {
                    null -> {}
                    else -> when (dueDate.compareTo(Now)) {
                        -1 -> add(Meta("due", "red", "calendar", "times", "outline", text = dueDate.toMomentString(false)))
                        +1 -> add(Meta("due", "calendar", "outline", text = dueDate.toMomentString(false)))
                        0 -> add(Meta("due", "yellow", "calendar", "outline", text = dueDate.toMomentString(false)))
                        else -> {}
                    }
                }
                when (val timeEstimate = task.timeEstimate) {
                    null -> {}
                    else -> add(Meta("estimated time", "hourglass", "outline", text = timeEstimate.toMomentString(false)))
                }
                when (val timeSpent = task.timeSpent) {
                    null -> {}
                    else -> when (task.timeEstimate?.compareTo(timeSpent)) {
                        -1 -> add(
                            Meta(
                                "spent time (critical)",
                                "red",
                                "stopwatch",
                                text = timeSpent.toMomentString(false)
                            )
                        )

                        else -> add(Meta("spent time", "stopwatch", text = timeSpent.toMomentString(false)))
                    }
                }
            }
        override val descriptions: Map<String, String?> get() = mapOf("Task" to task.description?.takeUnless { it.isBlank() })
        override val tags: Set<Tag> get() = task.tags.toSet()
    }
}
