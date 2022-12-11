package com.bkahlert.hello.debug.clickup

import com.bkahlert.hello.clickup.api.Assignee
import com.bkahlert.hello.clickup.api.CheckList
import com.bkahlert.hello.clickup.api.Creator
import com.bkahlert.hello.clickup.api.CustomField
import com.bkahlert.hello.clickup.api.Folder
import com.bkahlert.hello.clickup.api.FolderID
import com.bkahlert.hello.clickup.api.FolderPreview
import com.bkahlert.hello.clickup.api.Space
import com.bkahlert.hello.clickup.api.SpaceID
import com.bkahlert.hello.clickup.api.SpacePreview
import com.bkahlert.hello.clickup.api.Status
import com.bkahlert.hello.clickup.api.StatusID
import com.bkahlert.hello.clickup.api.StatusPreview
import com.bkahlert.hello.clickup.api.Tag
import com.bkahlert.hello.clickup.api.Task
import com.bkahlert.hello.clickup.api.TaskID
import com.bkahlert.hello.clickup.api.TaskLink
import com.bkahlert.hello.clickup.api.TaskList
import com.bkahlert.hello.clickup.api.TaskListID
import com.bkahlert.hello.clickup.api.TaskListPreview
import com.bkahlert.hello.clickup.api.TaskListPriority
import com.bkahlert.hello.clickup.api.TaskListStatus
import com.bkahlert.hello.clickup.api.TaskPreview
import com.bkahlert.hello.clickup.api.TaskPriority
import com.bkahlert.hello.clickup.api.Team
import com.bkahlert.hello.clickup.api.TeamID
import com.bkahlert.hello.clickup.api.TimeEntry
import com.bkahlert.hello.clickup.api.TimeEntryID
import com.bkahlert.hello.clickup.api.User
import com.bkahlert.hello.clickup.api.Watcher
import com.bkahlert.hello.clickup.api.asAssignee
import com.bkahlert.hello.clickup.api.asCreator
import com.bkahlert.hello.clickup.ui.Pomodoro
import com.bkahlert.hello.debug.ImageFixtures
import com.bkahlert.hello.deserialize
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.minus
import com.bkahlert.kommons.plus
import com.bkahlert.kommons.randomString
import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object ClickUpFixtures {

    val UserJson = """
        {
            "id": 11111,
            "username": "john.doe",
            "email": "john.doe@example.com",
            "color": "#ff0000",
            "profilePicture": "${ImageFixtures.JohnDoe}",
            "initials": "JD",
            "week_start_day": 1,
            "global_font_support": false,
            "timezone": "Europe/Berlin"
        }
        """.trimIndent()

    val User: User by lazy { UserJson.deserialize() }
    val Teams: List<Team> by lazy {
        """
        [
            {
                "id": "1111111",
                "name": "Pear",
                "color": "#00ff00",
                "avatar": "${ImageFixtures.PearLogo.dataURI}",
                "members": [
                    {
                        "user": $UserJson
                    }
                ]
            },
            {
                "id": "2222222",
                "name": "Kommons",
                "color": "#0000ff",
                "avatar": "${ImageFixtures.KommonsLogo.dataURI}",
                "members": [
                    {
                        "user": $UserJson
                    }
                ]
            }
        ]
        """.trimIndent().deserialize()
    }

    fun space(
        id: String,
        name: String,
        private: Boolean = false,
        statuses: List<Status> = listOf(
            Status(
                id = StatusID("p${id}_todo"),
                status = "to do",
                color = Color.RGB(0x02bcd4),
                orderIndex = 0,
                type = "open"
            ),
            Status(
                id = StatusID("p${id}_inprogress"),
                status = "in progress",
                color = Color.RGB(0xa875ff),
                orderIndex = 1,
                type = "custom"
            ),
            Status(
                id = StatusID("p${id}_closed"),
                status = "Closed",
                color = Color.RGB(0x6bc950),
                orderIndex = 2,
                type = "closed"
            ),
        ),
        multipleAssignees: Boolean = false,
    ) = Space(
        id = SpaceID(id),
        name = name,
        private = private,
        statuses = statuses,
        multipleAssignees = multipleAssignees,
    )

    val Spaces = listOf(
        space("1", "Home"),
        space("2", "Work"),
    )

    class TaskListBuilder {
        var id: String = randomString(3)
        var name: String = "Task list $id"
        var orderIndex: Int = 0
        var content: String? = null
        var status: TaskListStatus? = null
        var priority: TaskListPriority? = null
        var assignee: Assignee? = null
        var taskCount: Int? = null
        var dueDate: Date? = null
        var startDate: Date? = null
        var space: Space = Spaces.first()
        var archived: Boolean = false
        var overrideStatuses: Boolean? = true
        var permissionLevel: String = "create"

        companion object {
            fun build(folderInit: FolderBuilder? = null, init: TaskListBuilder.() -> Unit): TaskList {
                val builder = TaskListBuilder().apply(init)
                return TaskList(
                    id = TaskListID(builder.id),
                    name = builder.name,
                    orderIndex = builder.orderIndex,
                    content = builder.content,
                    status = builder.status,
                    priority = builder.priority,
                    assignee = builder.assignee,
                    taskCount = builder.taskCount,
                    dueDate = builder.dueDate,
                    startDate = builder.startDate,
                    folder = folderInit
                        ?.let {
                            FolderPreview(
                                id = FolderID(it.id),
                                name = it.name,
                                hidden = it.hidden,
                                access = true
                            )
                        }
                        ?: FolderPreview(
                            id = FolderID(randomString()),
                            name = randomString(),
                            hidden = true,
                            access = true
                        ),
                    space = builder.space.asPreview(),
                    archived = builder.archived,
                    overrideStatuses = builder.overrideStatuses,
                    permissionLevel = builder.permissionLevel,
                )
            }
        }
    }

    class FolderBuilder {
        var id: String = randomString(2)
        var name: String = "Folder $id"
        var orderIndex: Int = 0
        var overrideStatuses: Boolean = true
        var hidden: Boolean = false
        var archived: Boolean = false
        var permissionLevel: String = "create"
        var space: Space = Spaces.first()
        private val taskLists = mutableListOf<TaskListBuilder.() -> Unit>()
        fun taskList(init: TaskListBuilder.() -> Unit) {
            taskLists.add(init)
        }

        companion object {
            fun build(init: FolderBuilder.() -> Unit): Folder {
                val builder = FolderBuilder().apply(init)
                val taskLists = builder.taskLists.map { TaskListBuilder.build(builder, it) }.toList()
                return Folder(
                    id = FolderID(builder.id),
                    name = builder.name,
                    orderIndex = builder.orderIndex,
                    overrideStatuses = builder.overrideStatuses,
                    hidden = builder.hidden,
                    taskCount = taskLists.sumOf { it.taskCount ?: 0 }.toString(),
                    archived = builder.archived,
                    statuses = builder.space.statuses,
                    lists = taskLists,
                    permissionLevel = builder.permissionLevel,
                )
            }
        }
    }

    val Space1Folders: List<Folder> = buildList {
        add(FolderBuilder.build {
            name = "Friends"
            space = Spaces[0]
            taskList {
                name = "Close friends"
                taskCount = 3
                status = TaskListStatus("private", Color.Default.copy(hue = .2))
            }
            taskList {
                name = "Any friends"
                taskCount = 5
                status = TaskListStatus("private", Color.Default.copy(hue = .4))
            }
        })
    }

    val Space2Folders: List<Folder> = emptyList()

    val Space1FolderLists: List<TaskList> = Space1Folders.flatMap { it.lists }
    val Space2FolderLists: List<TaskList> = Space2Folders.flatMap { it.lists }

    val Space1FolderlessLists: List<TaskList> = buildList {
        add(TaskListBuilder.build {
            space = Spaces[0]
            name = "Programming"
            taskCount = 6
        })
    }
    val Space2FolderlessLists: List<TaskList> = buildList {
        add(TaskListBuilder.build {
            space = Spaces[1]
            name = "Company A"
            taskCount = 6
            status = TaskListStatus("private", Color.Default.copy(hue = .6))
        })
        add(TaskListBuilder.build {
            space = Spaces[1]
            name = "Company B"
            taskCount = 5
            status = TaskListStatus("private", Color.Default.copy(hue = .8))
        })
    }

    fun task(
        id: String = randomString(4),
        name: String = "Task $id",
        customId: String? = null,
        textContent: String? = "Text content of task $id",
        description: String? = "Description of task $id",
        status: StatusPreview = Spaces.first().statuses.first().asPreview(),
        orderIndex: Double? = null,
        dateCreated: Date? = Date() - 3.days,
        dateUpdated: Date? = null,
        dateClosed: String? = null,
        creator: Creator = User.asCreator(),
        assignees: List<Assignee> = listOf(User.asAssignee()),
        watchers: List<Watcher> = emptyList(),
        checklists: List<CheckList> = emptyList(),
        tags: List<Tag> = emptyList(),
        parent: TaskID? = null,
        priority: TaskPriority? = null,
        dueDate: Date? = null,
        startDate: Date? = null,
        points: Double? = null,
        timeEstimate: Duration? = 12.hours,
        timeSpent: Duration? = null,
        customFields: List<CustomField> = emptyList(),
        dependencies: List<String> = emptyList(),
        linkedTasks: List<TaskLink> = emptyList(),
        teamId: TeamID? = Teams.first().id,
        url: URL? = URL.parse("https://app.clickup.com/t/$id"),
        permissionLevel: String? = "create",
        list: TaskListPreview? = TaskListPreview(
            id = TaskListID("27814619"),
            name = "Inbox",
            access = true,
        ),
        folder: FolderPreview = FolderPreview(
            id = FolderID("13084360"),
            name = "hidden",
            hidden = true,
            access = true,
        ),
        space: SpacePreview = Spaces.first().asPreview(),
    ): Task {
        return Task(
            id = TaskID(id),
            customId = customId,
            name = name,
            textContent = textContent,
            description = description,
            status = status,
            orderIndex = orderIndex,
            dateCreated = dateCreated,
            dateUpdated = dateUpdated,
            dateClosed = dateClosed,
            creator = creator,
            assignees = assignees,
            watchers = watchers,
            checklists = checklists,
            tags = tags,
            parent = parent,
            priority = priority,
            dueDate = dueDate,
            startDate = startDate,
            points = points,
            timeEstimate = timeEstimate,
            timeSpent = timeSpent,
            customFields = customFields,
            dependencies = dependencies,
            linkedTasks = linkedTasks,
            teamId = teamId,
            url = url,
            permissionLevel = permissionLevel,
            list = list,
            folder = folder,
            space = space,
        )
    }

    fun tag(
        name: String,
        color: Color = Pomodoro.Type.Default.tag.foregroundColor.toHSL().randomize(hue = 1.0),
    ) = Tag(name = name, tagForeground = color, tagBackground = color, creator = User.id)

    val Tasks: List<Task> = buildList {
        (Space1FolderLists + Space1FolderlessLists + Space2FolderLists + Space2FolderlessLists).forEach { taskList ->
            val folder = checkNotNull(taskList.folder)
            Spaces.first { it.id == taskList.space?.id }.statuses.forEach { status ->
                add(
                    task(
                        list = taskList.asPreview(), folder = folder, space = checkNotNull(taskList.space), status = status.asPreview(),
                        name = "Task with status ${status.status}",
                        description = "This is a sample task with status ${status.status}",
                        startDate = when (status.status.lowercase()) {
                            "to do" -> null
                            else -> Date() - 2.days
                        },
                        timeSpent = when (status.status.lowercase()) {
                            "to do" -> null
                            "closed" -> 18.hours
                            else -> 6.hours
                        },
                        tags = when (status.status.lowercase()) {
                            "to do" -> emptyList()
                            "closed" -> listOf(Pomodoro.Type.values().random().tag, Pomodoro.Status.Completed.tag)
                            else -> listOf(Pomodoro.Type.values().random().tag)
                        },
                    )
                )
            }
            @Suppress("SpellCheckingInspection")
            add(
                task(
                    list = taskList.asPreview(), folder = folder, space = checkNotNull(taskList.space),
                    name = "Task with most meta data set",
                    description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.\nAt vero eos et accusam et justo duo dolores et ea rebum.<br><strong>Stet clita kasd gubergren,</strong> no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                    dateCreated = Date() - 3.days,
                    dateUpdated = Date() - 4.hours,
                    dateClosed = null,
                    tags = listOf(
                        Pomodoro.Type.Debug.tag,
                        tag("random tag ${randomString(2)}"),
                    ),
                    priority = TaskPriority.High,
                    dueDate = Date() + 2.days,
                    startDate = Date() - 3.days + 2.hours + 26.minutes,
                    points = 4.5,
                    timeEstimate = 12.hours,
                    timeSpent = 3.5.hours,
                )
            )

            add(
                task(
                    list = taskList.asPreview(), folder = folder, space = taskList.space,
                    name = "minimal task",
                    dateCreated = null,
                    dateUpdated = null,
                    dateClosed = null,
                    tags = emptyList(),
                    priority = null,
                    dueDate = null,
                    startDate = null,
                    points = null,
                    timeEstimate = null,
                    timeSpent = null,
                )
            )
        }
    }

    fun timeEntry(
        id: String = randomString(),
        task: TaskPreview? = Tasks[1].asPreview(),
        wid: TeamID = Teams.first().id,
        user: User = User,
        billable: Boolean = false,
        start: Date = Date() - Pomodoro.Type.Default.duration / 2,
        end: Date? = null,
        description: String = "A time entry",
        tags: List<Tag> = listOf(Pomodoro.Type.Default.tag),
        source: String? = null,
        taskUrl: URL? = URL.parse("https://app.clickup.com/t/${task?.id?.stringValue}"),
    ) = TimeEntry(
        id = TimeEntryID(id),
        task = task,
        wid = wid,
        user = user,
        billable = billable,
        start = start,
        end = end,
        description = description,
        tags = tags,
        source = source,
        taskUrl = taskUrl,
    )

    val TimeEntry: TimeEntry = timeEntry()

    fun TimeEntry.running(
        start: Date = Date() - 3.5.minutes,
        type: Pomodoro.Type? = null,
    ) = copy(
        start = start,
        tags = type?.addTag(tags) ?: tags
    )

    fun TimeEntry.aborted(
        start: Date = Date() - Pomodoro.Type.Default.duration / 2,
        end: Date = Date(),
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ) = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )

    fun TimeEntry.completed(
        start: Date = Date() - Pomodoro.Type.Default.duration,
        end: Date = Date(),
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ) = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )
}
