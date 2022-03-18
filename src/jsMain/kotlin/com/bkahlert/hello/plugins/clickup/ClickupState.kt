package com.bkahlert.hello.plugins.clickup

import com.bkahlert.Brand
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Response
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.Success
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.Activity.RunningTaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.Activity.TaskActivity
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activated.ActivityGroup.Meta
import com.bkahlert.hello.plugins.clickup.ClickupState.Loaded.Activating
import com.bkahlert.hello.ui.errorMessage
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Color.RGB
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.fix.map
import com.bkahlert.kommons.fix.or
import com.bkahlert.kommons.fix.orNull
import com.bkahlert.kommons.fix.value
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.compareTo
import com.bkahlert.kommons.time.minus
import com.bkahlert.kommons.time.toMoment
import com.clickup.api.ClickupList
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.Space.ID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.ClickupClient
import com.clickup.api.rest.Identifier
import io.ktor.http.Url

sealed interface ClickupState {
    object Disconnected : ClickupState

    object Loading : ClickupState

    sealed class Loaded(
        protected open val client: ClickupClient,
        open val user: User,
        open val teams: List<Team>,
        protected open val update: (suspend (ClickupState) -> ClickupState) -> Unit,
    ) : ClickupState {

        protected val logger = simpleLogger()

        fun activate(team: Team) {
            logger.info("Activating ${user.username}@${team.name}")
            update { Activated(client, user, teams, team, client.getRunningTimeEntry(team, user), update = update) }
        }

        data class Activating(
            override val client: ClickupClient,
            override val user: User,
            override val teams: List<Team>,
            override val update: (suspend (ClickupState) -> ClickupState) -> Unit,
        ) : Loaded(client, user, teams, update)

        data class Activated(
            override val client: ClickupClient,
            override val user: User,
            override val teams: List<Team>,
            val activeTeam: Team,
            val runningTimeEntry: Response<TimeEntry?>,
            val tasks: Response<List<Task>> = null,
            val spaces: Response<List<Space>> = null,
            val folders: Map<ID, Either<List<Folder>, Throwable>> = emptyMap(),
            val spaceLists: Map<ID, Response<List<ClickupList>>> = emptyMap(),
            val folderLists: Map<Folder.ID, Response<List<ClickupList>>> = emptyMap(),
            override val update: (suspend (ClickupState) -> ClickupState) -> Unit,
        ) : Loaded(client, user, teams, update) {

            fun prepare() {
                update {
                    val tasks = client.getTasks(activeTeam)
                    val spaces = client.getSpaces(activeTeam)
                    val folders = spaces.map { it.associate { space -> space.id to client.getFolders(space) } } or { emptyMap() }
                    val spaceLists = spaces.map { it.associate { space -> space.id to client.getLists(space) } } or { emptyMap() }
                    val folderLists = buildMap<Folder.ID, Response<List<ClickupList>>> {
                        folders.values.forEach { folderRequests ->
                            putAll(folderRequests.map { folderList ->
                                folderList.associate { folder ->
                                    folder.id to client.getLists(folder)
                                }
                            } or { emptyMap() })
                        }
                    }
                    copy(tasks = tasks, spaces = spaces, folders = folders, spaceLists = spaceLists, folderLists = folderLists)
                }
            }

            data class ActivityGroup(
                val name: List<Meta>,
                val color: Color?,
                val tasks: List<Activity<*>>,
            ) {
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
                }
            }

            sealed interface Activity<ID : Identifier<*>> {
                val id: ID?
                val name: String
                val color: Color?
                val url: Url?
                val meta: List<Meta>
                val descriptions: Map<String, String?>
                val tags: Set<Tag>

                data class RunningTaskActivity(
                    val timeEntry: TimeEntry,
                    val task: Task? = null,
                ) : Activity<TimeEntry.ID> {
                    private val taskActivity: TaskActivity? = task?.let(::TaskActivity)
                    override val id: TimeEntry.ID get() = timeEntry.id
                    override val name: String get() = timeEntry.task?.name ?: taskActivity?.name ?: "Timer running"
                    override val color: Color? get() = timeEntry.task?.status?.color ?: taskActivity?.color
                    override val url: Url? get() = timeEntry.url ?: timeEntry.taskUrl ?: taskActivity?.url
                    override val meta: List<Meta>
                        get() = buildList {
                            add(Meta("running since", "stopwatch", text = (Now - timeEntry.start).toMoment(false)))
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
                ) : Activity<Task.ID> {
                    override val id: Task.ID? get() = task.id
                    override val name: String get() = task.name
                    override val color: Color get() = task.status.color
                    override val url: Url? get() = task.url
                    override val meta: List<Meta>
                        get() = buildList {
                            if (task.dateCreated != null) {
                                add(Meta("created", "calendar", "alternate", "outline", text = task.dateCreated.toMoment()))
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
                            when (task.dueDate?.compareTo(Now)) {
                                -1 -> add(Meta("due", "red", "calendar", "times", "outline", text = task.dueDate.toMoment(false)))
                                +1 -> add(Meta("due", "calendar", "outline", text = task.dueDate.toMoment(false)))
                                0 -> add(Meta("due", "yellow", "calendar", "outline", text = task.dueDate.toMoment(false)))
                                else -> {}
                            }
                        }
                    override val descriptions: Map<String, String?> get() = mapOf("Task" to task.description?.takeUnless { it.isBlank() })
                    override val tags: Set<Tag> get() = task.tags.toSet()
                }
            }

            val runningActivity: RunningTaskActivity? by lazy {
                val runningTimeEntry: TimeEntry? = runningTimeEntry?.orNull()
                val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                runningTimeEntry?.let { timeEntry ->
                    RunningTaskActivity(timeEntry, timeEntry.task?.run { tasks.firstOrNull { it.id == id } })
                }
            }

            fun activity(id: String) = activities.firstNotNullOf { (_, _, activities) ->
                activities.firstOrNull { it.id?.stringValue == id }
            }

            val activities: List<ActivityGroup> by lazy {

                buildList {
                    runningActivity?.also {
                        add(ActivityGroup(
                            listOf(Meta("running timer", "stop", "circle", text = "Running")),
                            Brand.colors.red,
                            listOf(it),
                        ))
                    }

                    val tasks: List<Task> = tasks?.orNull() ?: emptyList()
                    val spaces = spaces?.orNull() ?: emptyList()

                    tasks.groupBy { it.space.id }
                        .forEach { (spaceID, spaceTasks) ->
                            val spaceMeta = Meta("project", "clone", text = spaces.firstOrNull { it.id == spaceID }?.name ?: "[no space name]")
                            spaceTasks.groupBy { it.folder }
                                .forEach { (folderPreview, folderTasks) ->
                                    val folderMeta = if (folderPreview.hidden) null else Meta("folder", "folder", text = folderPreview.name)
                                    folderTasks.groupBy { it.list }
                                        .forEach { (listPreview, listTasks) ->
                                            val lists = when {
                                                folderPreview.hidden -> spaceLists[spaceID]
                                                else -> folderLists[folderPreview.id]
                                            }?.orNull() ?: emptyList()

                                            val (listName, listColor) = lists
                                                .firstOrNull { it.id == listPreview?.id }
                                                ?.run { name to status?.color }
                                                ?: ((listPreview?.name ?: "[no list]") to null)

                                            val listMeta = Meta("list", "list", text = listName)
                                            add(ActivityGroup(
                                                listOfNotNull(spaceMeta, folderMeta, listMeta),
                                                listColor,
                                                listTasks.map { TaskActivity(it) },
                                            ))
                                        }
                                }
                        }
                }
            }

            fun startTimeEntry() {
                val tomato = RGB(0xff6347)
                val tomatoSauce = RGB(0xb21807)
                update {
                    logger.log("starting time entry")
                    val xxx: Either<TimeEntry, Throwable> = client.startTimeEntry(
                        activeTeam,
                        Task.ID("20jg1er"),
                        "50m pomodoro",
                        false,
                        Tag("pomodoro", tomato, tomato, null),
                    )
                    copy(runningTimeEntry = xxx)
                }
            }

            fun stopTimeEntry() {
                update {
                    logger.info("stopping time entry")
                    when (val response = client.stopTimeEntry(activeTeam)) {
                        is Success -> console.log("stopped", response.value)
                        is Failure -> console.error("failed to stop", response.value.errorMessage)
                    }
                    this
                }
            }
        }
    }

    data class Failed(
        val exceptions: List<Throwable>,
    ) : ClickupState {
        constructor(vararg exceptions: Throwable) : this(exceptions.toList())

        val message: String
            get() = exceptions.firstNotNullOfOrNull { it.message } ?: "message missing"
    }

    companion object {
        fun of(
            client: ClickupClient,
            userResponse: Response<User>,
            teamsResponse: Response<List<Team>>,
            update: (suspend (ClickupState) -> ClickupState) -> Unit,
        ): ClickupState {
            if (userResponse == null || teamsResponse == null) return Loading
            return userResponse.map { user ->
                teamsResponse.map { Activating(client, user, it, update) } or { Failed(it) }
            } or { ex ->
                userResponse.map { Failed(ex) } or { Failed(ex, it) }
            }
        }
    }
}
