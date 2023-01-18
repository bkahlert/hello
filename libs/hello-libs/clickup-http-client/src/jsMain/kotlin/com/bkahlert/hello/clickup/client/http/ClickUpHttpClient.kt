package com.bkahlert.hello.clickup.client.http

import com.bkahlert.hello.clickup.model.ClickUpClient
import com.bkahlert.hello.clickup.model.ClickUpException
import com.bkahlert.hello.clickup.model.CustomFieldFilter
import com.bkahlert.hello.clickup.model.Folder
import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.Space
import com.bkahlert.hello.clickup.model.SpaceID
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.Task
import com.bkahlert.hello.clickup.model.TaskID
import com.bkahlert.hello.clickup.model.TaskList
import com.bkahlert.hello.clickup.model.TaskListID
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.model.User
import com.bkahlert.hello.clickup.model.div
import com.bkahlert.hello.clickup.serialization.Named
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.json.Lenient
import com.bkahlert.kommons.json.serialize
import com.bkahlert.kommons.logging.InlineLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.Date

public data class ClickUpHttpClient(
    val accessToken: PersonalAccessToken,
    private val cacheStorage: Storage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ClickUpClient {
    //    val clickUpUrl = Url("/api.clickup.com/api/v2")
    val clickUpUrl: Url = Url("https://hello.aws-dev.choam.de/api.clickup.com/api/v2")

    private val logger by InlineLogging

    init {
        logger.debug("initializing ClickUp client with access token")
    }

    private val restClient by lazy {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json(Json.Lenient)
            }
            HttpResponseValidator {
                handleResponseExceptionWithRequest { ex, _ ->
                    logger.error("response validation", ex)
                    throw when (ex) {
                        is ResponseException -> {
                            kotlin
                                .runCatching { ex.response.body<ErrorInfo>() }
                                .map { (err, eCode) -> ClickUpException(err, eCode, ex) }
                                .getOrElse { ex }
                        }

                        else -> ex
                    }
                }
            }
            install("ClickUp-PersonalToken-Authorization") {
                plugin(HttpSend).intercept { context ->
                    logger.debug("setting ${HttpHeaders.Authorization}=$accessToken")
                    context.headers[HttpHeaders.Authorization] = accessToken.token
                    execute(context)
                }
            }
        }
    }

    private suspend fun <T> runLogging(
        name: String,
        block: suspend () -> T,
    ): T = withContext(dispatcher) {
        kotlin.runCatching {
            logger.debug(name)
            block()
        }.getOrElse {
            logger.error("ClickUp error occurred", it)
            throw it
        }
    }

    private val cache = Cache(cacheStorage)

    private suspend inline fun <reified T> HttpClient.caching(
        provideAccessor: (Cache) -> CacheAccessor,
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {},
    ): T {
        val accessor = provideAccessor(cache)
        val cached = accessor.load<T>()
        if (cached != null) return cached
        return get(url, block).body<T>().also { accessor.save(it) }
    }

    override suspend fun getUser(): User =
        runLogging("getting user") {
            restClient.caching<Named<User>>({ it.forUser() }, clickUpUrl / "user").value
        }

    override suspend fun getTeams(): List<Team> =
        runLogging("getting teams") {
            restClient.caching<Named<List<Team>>>({ it.forTeams() }, clickUpUrl / "team").value
        }

    override suspend fun createTask(listId: TaskListID, name: String): Task =
        runLogging("creating task $name in $listId") {
            cache.clear()
            restClient.post(clickUpUrl / "list" / listId / "task") {
                contentType(ContentType.Application.Json)
                setBody(CreateTaskRequest(name))
            }.body()
        }

    override suspend fun getTasks(
        team: Team,
        page: Int?,
        order_by: String?,
        reverse: Boolean?,
        subtasks: Boolean?,
        space_ids: List<SpaceID>?,
        project_ids: List<FolderID>?,
        list_ids: List<TaskListID>?,
        statuses: List<String>?,
        include_closed: Boolean?,
        assignees: List<String>?,
        tags: List<String>?,
        due_date_gt: Date?,
        due_date_lt: Date?,
        date_created_gt: Date?,
        date_created_lt: Date?,
        date_updated_gt: Date?,
        date_updated_lt: Date?,
        custom_fields: List<CustomFieldFilter>?,
    ): List<Task> =
        runLogging("getting tasks for team=${team.name}") {
            restClient.caching<Named<List<Task>>>({ it.forTasks(team.id) }, clickUpUrl / "team" / team.id / "task") {
                parameter("page", page)
                parameter("order_by", order_by)
                parameter("reverse", reverse)
                parameter("subtasks", subtasks)
                space_ids?.forEach { parameter("space_ids", it) }
                project_ids?.forEach { parameter("project_ids", it) }
                list_ids?.forEach { parameter("list_ids", it) }
                statuses?.forEach { parameter("statuses", it) }
                parameter("include_closed", include_closed)
                assignees?.forEach { parameter("assignees", it) }
                tags?.forEach { parameter("tags", it) }
                parameter("due_date_gt", due_date_gt)
                parameter("due_date_lt", due_date_lt)
                parameter("date_created_gt", date_created_gt)
                parameter("date_created_lt", date_created_lt)
                parameter("date_updated_gt", date_updated_gt)
                parameter("date_updated_lt", date_updated_lt)
                custom_fields?.forEach { parameter("custom_fields", it) }
                custom_fields?.forEach { parameter("custom_fields", it.serialize(pretty = false)) }
            }.value
        }

    override suspend fun getTask(
        taskId: TaskID,
    ): Task? =
        runLogging("getting task $taskId") {
            restClient.get(clickUpUrl / "task" / taskId).body()
        }

    override suspend fun updateTask(task: Task): Task =
        runLogging("updating task ${task.id}") {
            task.teamId?.also { cache.forTasks(it).evict() }
            restClient.put(clickUpUrl / "task" / task.id) {
                contentType(ContentType.Application.Json)
                setBody(UpdateTaskRequest(status = task.status.status))
            }.body()
        }

    override suspend fun getSpaces(
        team: Team,
        archived: Boolean,
    ): List<Space> =
        runLogging("getting spaces for team=${team.name}") {
            restClient.caching<Named<List<Space>>>({ it.forSpaces(team.id) }, clickUpUrl / "team" / team.id / "space") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getLists(
        space: Space,
        archived: Boolean,
    ): List<TaskList> =
        runLogging("getting lists for space=${space.name}") {
            restClient.caching<Named<List<TaskList>>>({ it.forSpaceLists(space.id) }, clickUpUrl / "space" / space.id / "list") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getFolders(
        space: Space,
        archived: Boolean,
    ): List<Folder> =
        runLogging("getting folders for space=${space.name}") {
            restClient.caching<Named<List<Folder>>>({ it.forFolders(space.id) }, clickUpUrl / "space" / space.id / "folder") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getFolder(
        folderId: FolderID,
    ): Folder =
        runLogging("getting folder $folderId") {
            restClient.caching({ it.forFolder(folderId) }, clickUpUrl / "folder" / folderId)
        }

    override suspend fun getLists(
        folder: Folder,
        archived: Boolean,
    ): List<TaskList> =
        runLogging("getting lists for folder=${folder.name}") {
            restClient.caching<Named<List<TaskList>>>({ it.forFolderLists(folder.id) }, clickUpUrl / "folder" / folder.id / "list") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getTimeEntry(
        team: Team,
        timeEntryID: TimeEntryID,
    ): TimeEntry? =
        runLogging("getting time entry $timeEntryID for team=${team.name}") {
            restClient.get(clickUpUrl / "team" / team.id / "time_entries" / timeEntryID).body<Named<TimeEntry?>>().value
        }

    override suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
    ): TimeEntry? =
        runLogging("getting running time entry for team=${team.name} and assignee=${assignee?.username}") {
            restClient.caching<Named<TimeEntry?>>({ it.forRunningTimeEntry(team.id) }, clickUpUrl / "team" / team.id / "time_entries" / "current") {
                parameter("assignee", assignee?.id?.stringValue)
            }.value
        }

    override suspend fun startTimeEntry(
        team: Team,
        taskId: TaskID?,
        description: String?,
        billable: Boolean,
        vararg tags: Tag,
    ): TimeEntry =
        runLogging("starting time entry of task=${taskId?.stringValue ?: "<no task>"} for team=${team.name}") {
            cache.forRunningTimeEntry(team.id).evict()
            restClient.post(clickUpUrl / "team" / team.id / "time_entries" / "start") {
                contentType(ContentType.Application.Json)
                setBody(StartTimeEntryRequest(taskId, description, billable, tags.toList()))
            }.body<Named<TimeEntry>>().value
        }

    override suspend fun stopTimeEntry(
        team: Team,
    ): TimeEntry? =
        runLogging("stopping time entry for team=${team.name}") {
            cache.forRunningTimeEntry(team.id).evict()
            restClient.post(clickUpUrl / "team" / team.id / "time_entries" / "stop").body<Named<TimeEntry?>>().value
        }

    override suspend fun addTagsToTimeEntries(
        team: Team,
        timeEntryIDs: List<TimeEntryID>,
        tags: List<Tag>,
    ): Unit =
        runLogging("adding tags $tags to time entries $timeEntryIDs for team=${team.name}") {
            cache.forRunningTimeEntry(team.id).evict()
            restClient.post(clickUpUrl / "team" / team.id / "time_entries" / "tags") {
                contentType(ContentType.Application.Json)
                setBody(AddTagsToTimeEntriesRequest(timeEntryIDs, tags))
            }
        }

    public companion object
}

@Serializable
private data class ErrorInfo(
    val err: String,
    val ECODE: String,
)


@Serializable
private data class StartTimeEntryRequest(
    val tid: TaskID?,
    val description: String?,
    val billable: Boolean,
    val tags: List<Tag>,
)

@Serializable
private data class AddTagsToTimeEntriesRequest(
    @SerialName("time_entry_ids") val timeEntryIDs: List<TimeEntryID>,
    @SerialName("tags") val tags: List<Tag>,
)

@Serializable
private data class CreateTaskRequest(
    @SerialName("name") val name: String,
)

@Serializable
private data class UpdateTaskRequest(
    @SerialName("status") val status: String?,
)
