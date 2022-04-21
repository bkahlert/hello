package com.clickup.api.rest

import com.bkahlert.hello.JsonSerializer
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.asString
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.div
import com.bkahlert.kommons.serialization.Named
import com.clickup.api.Folder
import com.clickup.api.FolderID
import com.clickup.api.Space
import com.clickup.api.SpaceID
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.TaskID
import com.clickup.api.TaskList
import com.clickup.api.TaskListID
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import com.clickup.api.User
import com.clickup.api.div
import com.clickup.api.rest.Cache.Accessor
import com.clickup.api.rest.ClickUpException.Companion.wrapOrNull
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.js.Date

data class AccessTokenBasedClickUpClient(
    val accessToken: AccessToken,
    private val cacheStorage: Storage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ClickUpClient {
    val clickUpUrl = Url("/api.clickup.com/api/v2")

    private val logger = simpleLogger()

    init {
        logger.debug("initializing ClickUp client with access token")
    }

    private val restClient by lazy {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json(JsonSerializer)
            }
            HttpResponseValidator {
                handleResponseException { throw it.wrapOrNull() ?: it }
            }
            install("ClickUp-PersonalToken-Authorization") {
                plugin(HttpSend).intercept { context ->
                    logger.debug("setting ${HttpHeaders.Authorization}=$accessToken")
                    accessToken.configue(context)
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
        provideAccessor: Cache.() -> Accessor,
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {},
    ): T {
        val accessor = cache.provideAccessor()
        val cached = accessor.load<T>()
        if (cached != null) return cached
        return get(url, block).body<T>().also(accessor::save)
    }

    override suspend fun getUser(): User =
        runLogging("getting user") {
            restClient.caching<Named<User>>({ forUser() }, clickUpUrl / "user").value
        }

    override suspend fun getTeams(): List<Team> =
        runLogging("getting teams") {
            restClient.caching<Named<List<Team>>>({ forTeams() }, clickUpUrl / "team").value
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
            restClient.caching<Named<List<Task>>>({ forTasks(team.id) }, clickUpUrl / "team" / team.id / "task") {
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

    override suspend fun getSpaces(
        team: Team,
        archived: Boolean,
    ): List<Space> =
        runLogging("getting spaces for team=${team.name}") {
            restClient.caching<Named<List<Space>>>({ forSpaces(team.id) }, clickUpUrl / "team" / team.id / "space") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getLists(
        space: Space,
        archived: Boolean,
    ): List<TaskList> =
        runLogging("getting lists for space=${space.name}") {
            restClient.caching<Named<List<TaskList>>>({ forSpaceLists(space.id) }, clickUpUrl / "space" / space.id / "list") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getFolders(
        space: Space,
        archived: Boolean,
    ): List<Folder> =
        runLogging("getting folders for space=${space.name}") {
            restClient.caching<Named<List<Folder>>>({ forFolders(space.id) }, clickUpUrl / "space" / space.id / "folder") {
                parameter("archived", archived)
            }.value
        }

    override suspend fun getLists(
        folder: Folder,
        archived: Boolean,
    ): List<TaskList> =
        runLogging("getting lists for folder=${folder.name}") {
            restClient.caching<Named<List<TaskList>>>({ forFolderLists(folder.id) }, clickUpUrl / "folder" / folder.id / "list") {
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
            restClient.caching<Named<TimeEntry?>>({ forRunningTimeEntry(team.id) }, clickUpUrl / "team" / team.id / "time_entries" / "current") {
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

    override fun toString(): String = asString()
}
