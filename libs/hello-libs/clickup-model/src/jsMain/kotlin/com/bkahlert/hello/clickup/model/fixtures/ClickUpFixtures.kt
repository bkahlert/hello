package com.bkahlert.hello.clickup.model.fixtures

import com.bkahlert.hello.clickup.Pomodoro
import com.bkahlert.hello.clickup.Pomodoro.Status.Completed
import com.bkahlert.hello.clickup.Pomodoro.Type
import com.bkahlert.hello.clickup.Pomodoro.Type.Debug
import com.bkahlert.hello.clickup.model.Assignee
import com.bkahlert.hello.clickup.model.CheckList
import com.bkahlert.hello.clickup.model.Creator
import com.bkahlert.hello.clickup.model.CustomField
import com.bkahlert.hello.clickup.model.Folder
import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.FolderPreview
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.SpaceID
import com.bkahlert.hello.clickup.model.SpacePreview
import com.bkahlert.hello.clickup.model.Status
import com.bkahlert.hello.clickup.model.StatusID
import com.bkahlert.hello.clickup.model.StatusPreview
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskLink
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.TaskListPreview
import com.bkahlert.hello.clickup.model.TaskListPriority
import com.bkahlert.hello.clickup.model.TaskListStatus
import com.bkahlert.hello.clickup.model.TaskPreview
import com.bkahlert.hello.clickup.model.TaskPriority
import com.bkahlert.hello.clickup.model.TaskPriority.High
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TeamID
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.UserID
import com.bkahlert.hello.clickup.model.Watcher
import com.bkahlert.hello.clickup.model.asAssignee
import com.bkahlert.hello.clickup.model.asCreator
import com.bkahlert.hello.clickup.serialization.Named
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

public object ClickUpFixtures {

    public val UserJson: String = """
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

    public val User: User = User(
        id = UserID(11111),
        username = "john.doe",
        email = "john.doe@example.com",
        color = Color(0xff0000),
        profilePicture = ImageFixtures.JohnDoe,
        initials = "JD",
        weekStartDay = 1,
        globalFontSupport = false,
        timezone = "Europe/Berlin"
    )

    public val Teams: List<Team> by lazy {
        listOf(
            Team(
                id = TeamID("1111111"),
                name = "Pear",
                color = Color(0x0ff00),
                avatar = ImageFixtures.PearLogo,
                members = listOf(Named.ofSingle(User)),
            ),
            Team(
                id = TeamID("2222222"),
                name = "Kommons",
                color = Color(0x0ff00),
                avatar = ImageFixtures.KommonsLogo,
                members = listOf(Named.ofSingle(User)),
            ),
        )
    }

    public fun space(
        id: String,
        name: String,
        private: Boolean = false,
        statuses: List<Status> = listOf(
            Status(
                id = StatusID("p${id}_todo"),
                status = "to do",
                color = RGB(0x02bcd4),
                orderIndex = 0,
                type = "open"
            ),
            Status(
                id = StatusID("p${id}_inprogress"),
                status = "in progress",
                color = RGB(0xa875ff),
                orderIndex = 1,
                type = "custom"
            ),
            Status(
                id = StatusID("p${id}_closed"),
                status = "Closed",
                color = RGB(0x6bc950),
                orderIndex = 2,
                type = "closed"
            ),
        ),
        multipleAssignees: Boolean = false,
    ): Space = Space(
        id = SpaceID(id),
        name = name,
        private = private,
        statuses = statuses,
        multipleAssignees = multipleAssignees,
    )

    public val Spaces: List<Space> = listOf(
        space("1", "Home"),
        space("2", "Work"),
    )

    public class TaskListBuilder {
        public var id: String = randomString(3)
        public var name: String = "Task list $id"
        public var orderIndex: Int = 0
        public var content: String? = null
        public var status: TaskListStatus? = null
        public var priority: TaskListPriority? = null
        public var assignee: Assignee? = null
        public var taskCount: Int? = null
        public var dueDate: Instant? = null
        public var startDate: Instant? = null
        public var space: Space = Spaces.first()
        public var archived: Boolean = false
        public var overrideStatuses: Boolean? = true
        public var permissionLevel: String = "create"

        public companion object {
            public fun build(folderInit: FolderBuilder? = null, init: TaskListBuilder.() -> Unit): TaskList {
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

    public class FolderBuilder {
        public var id: String = randomString(2)
        public var name: String = "Folder $id"
        public var orderIndex: Int = 0
        public var overrideStatuses: Boolean = true
        public var hidden: Boolean = false
        public var archived: Boolean = false
        public var permissionLevel: String = "create"
        public var space: Space = Spaces.first()
        private val taskLists = mutableListOf<TaskListBuilder.() -> Unit>()
        public fun taskList(init: TaskListBuilder.() -> Unit) {
            taskLists.add(init)
        }

        public companion object {
            public fun build(init: FolderBuilder.() -> Unit): Folder {
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

    public val Space1Folders: List<Folder> = buildList {
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

    public val Space2Folders: List<Folder> = emptyList()

    public val Space1FolderLists: List<TaskList> = Space1Folders.flatMap { it.lists }
    public val Space2FolderLists: List<TaskList> = Space2Folders.flatMap { it.lists }

    public val Space1FolderlessLists: List<TaskList> = buildList {
        add(TaskListBuilder.build {
            space = Spaces[0]
            name = "Programming"
            taskCount = 6
        })
    }
    public val Space2FolderlessLists: List<TaskList> = buildList {
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

    public fun task(
        id: String = randomString(4),
        name: String = "Task $id",
        customId: String? = null,
        textContent: String? = "Text content of task $id",
        description: String? = "Description of task $id",
        status: StatusPreview = Spaces.first().statuses.first().asPreview(),
        orderIndex: Double? = null,
        dateCreated: Instant? = Now - 3.days,
        dateUpdated: Instant? = null,
        dateClosed: Instant? = null,
        creator: Creator = User.asCreator(),
        assignees: List<Assignee> = listOf(User.asAssignee()),
        watchers: List<Watcher> = emptyList(),
        checklists: List<CheckList> = emptyList(),
        tags: List<Tag> = emptyList(),
        parent: TaskID? = null,
        priority: TaskPriority? = null,
        dueDate: Instant? = null,
        startDate: Instant? = null,
        points: Double? = null,
        timeEstimate: Duration? = 12.hours,
        timeSpent: Duration? = null,
        customFields: List<CustomField> = emptyList(),
        dependencies: List<String> = emptyList(),
        linkedTasks: List<TaskLink> = emptyList(),
        teamId: TeamID? = Teams.first().id,
        url: Uri? = Uri.parse("https://app.clickup.com/t/$id"),
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

    public fun tag(
        name: String,
        color: Color = Pomodoro.Type.Default.tag.foregroundColor.toHSL().randomize(hue = 1.0),
    ): Tag = Tag(name = name, tagForeground = color, tagBackground = color, creator = User.id)

    public val Tasks: List<Task> = buildList {
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
                            else -> Now - 2.days
                        },
                        timeSpent = when (status.status.lowercase()) {
                            "to do" -> null
                            "closed" -> 18.hours
                            else -> 6.hours
                        },
                        tags = when (status.status.lowercase()) {
                            "to do" -> emptyList()
                            "closed" -> listOf(Type.values().random().tag, Completed.tag)
                            else -> listOf(Type.values().random().tag)
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
                    dateCreated = Now - 3.days,
                    dateUpdated = Now - 4.hours,
                    dateClosed = null,
                    tags = listOf(
                        Debug.tag,
                        tag("random tag ${randomString(2)}"),
                    ),
                    priority = High,
                    dueDate = Now + 2.days,
                    startDate = Now - 3.days + 2.hours + 26.minutes,
                    points = 4.5,
                    timeEstimate = 12.hours,
                    timeSpent = 3.5.hours,
                )
            )

            add(
                task(
                    list = taskList.asPreview(), folder = folder, space = checkNotNull(taskList.space),
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

    public fun timeEntry(
        id: String = randomString(),
        task: TaskPreview? = Tasks[1].asPreview(),
        wid: TeamID = Teams.first().id,
        user: User = User,
        billable: Boolean = false,
        start: Instant = Now - Pomodoro.Type.Default.duration / 2,
        end: Instant? = null,
        description: String = "A time entry",
        tags: List<Tag> = listOf(Pomodoro.Type.Default.tag),
        source: String? = null,
        taskUrl: Uri? = Uri.parse("https://app.clickup.com/t/${task?.id?.stringValue}"),
    ): TimeEntry = TimeEntry(
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

    public val TimeEntry: TimeEntry = timeEntry()

    public fun TimeEntry.running(
        start: Instant = Now - 3.5.minutes,
        type: Pomodoro.Type? = null,
    ): TimeEntry = copy(
        start = start,
        tags = type?.addTag(tags) ?: tags
    )

    public fun TimeEntry.aborted(
        start: Instant = Now - Pomodoro.Type.Default.duration / 2,
        end: Instant = Now,
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ): TimeEntry = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )

    public fun TimeEntry.completed(
        start: Instant = Now - Pomodoro.Type.Default.duration,
        end: Instant = Now,
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ): TimeEntry = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )
}
