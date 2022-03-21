package com.clickup.api.rest

import com.bkahlert.hello.Failure
import com.bkahlert.hello.JsonSerializer
import com.bkahlert.hello.Response
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.Success
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.runtime.LocalStorage
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.web.http.div
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
import com.clickup.api.TeamID
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import com.clickup.api.User
import com.clickup.api.rest.ClickUpException.Companion.wrapOrNull
import com.clickup.api.rest.ClickupClient.Cache.FOLDERS
import com.clickup.api.rest.ClickupClient.Cache.FOLDER_LISTS
import com.clickup.api.rest.ClickupClient.Cache.RUNNING_TIME_ENTRY
import com.clickup.api.rest.ClickupClient.Cache.SPACES
import com.clickup.api.rest.ClickupClient.Cache.SPACE_LISTS
import com.clickup.api.rest.ClickupClient.Cache.TASKS
import com.clickup.api.rest.ClickupClient.Cache.TEAMS
import com.clickup.api.rest.ClickupClient.Cache.USER
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date

data class ClickupClient(
    private val accessToken: AccessToken,
) {
    //    val clickUpUrl = "https://api.clickup.com/api/v2"
    val clickUpUrl = Url("http://localhost:8080/api/v2")

    private val logger = simpleLogger()

    init {
        logger.debug("initializing ClickUp client with access token")
    }

    private val tokenClient by lazy {
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

    private suspend fun <T> inBackground(
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit = {},
        block: suspend () -> T,
    ): Response<T> =
        try {
            val success = block()
            onSuccess(success)
            Success(success)
        } catch (e: Exception) {
            logger.error("ClickUp error occurred", e)
            onFailure(e)
            Failure(e)
        }

    private sealed class Cache(
        private val key: String,
    ) {
        object USER : Cache("clickup-user")
        object TEAMS : Cache("clickup-teams")
        data class RUNNING_TIME_ENTRY(val id: TeamID) : Cache("clickup-running-time-entry-${id.stringValue}")
        data class TASKS(val id: TeamID) : Cache("clickup-team-tasks-${id.stringValue}")
        data class SPACES(val id: TeamID) : Cache("clickup-team-spaces-${id.stringValue}")
        data class SPACE_LISTS(val id: SpaceID) : Cache("clickup-space-lists-${id.stringValue}")
        data class FOLDERS(val id: SpaceID) : Cache("clickup-space-folders-${id.stringValue}")
        data class FOLDER_LISTS(val id: FolderID) : Cache("clickup-folder-lists-${id.stringValue}")

        private val logger = simpleLogger()

        inline fun <reified T> load(): T? = LocalStorage[key]
            ?.runCatching { deserialize<T>()?.also { logger.debug("successfully loaded cached response for $key") } }
            ?.onFailure { logger.warn("failed to load cached response for $key", it) }
            ?.getOrNull()

        inline fun <reified T> save(value: T) {
            logger.debug("caching response for $key")
            kotlin.runCatching {
                LocalStorage[key] = value.serialize()
                logger.debug("successfully cached response for $key")
            }.onFailure {
                logger.warn("failed to cache response for $key")
            }.getOrNull()
        }

        fun evict() {
            LocalStorage.remove(key)
            logger.debug("removed cache entry for $key")
        }
    }

    private suspend inline fun <reified T> HttpClient.caching(
        cache: Cache,
        url: Url,
        block: HttpRequestBuilder.() -> Unit = {},
    ): T {
        val cached = cache.load<T>()
        if (cached != null) return cached
        return get(url, block).body<T>().also(cache::save)
    }

    suspend fun getUser(onSuccess: (User) -> Unit = {}): Response<User> =
        inBackground(onSuccess) {
            logger.debug("getting user")
            tokenClient.caching<Named<User>>(USER, clickUpUrl / "user").value
        }

    suspend fun getTeams(onSuccess: (List<Team>) -> Unit = {}): Response<List<Team>> =
        inBackground(onSuccess) {
            logger.debug("getting teams")
            tokenClient.caching<Named<List<Team>>>(TEAMS, clickUpUrl / "team").value
        }

    suspend fun getTasks(
        team: Team,
        page: Int? = null,
        order_by: String? = null,
        reverse: Boolean? = null,
        subtasks: Boolean? = null,
        space_ids: List<SpaceID>? = null,
        project_ids: List<FolderID>? = null,
        list_ids: List<TaskListID>? = null,
        statuses: List<String>? = null,
        include_closed: Boolean? = null,
        assignees: List<String>? = null,
        tags: List<String>? = null,
        due_date_gt: Date? = null,
        due_date_lt: Date? = null,
        date_created_gt: Date? = null,
        date_created_lt: Date? = null,
        date_updated_gt: Date? = null,
        date_updated_lt: Date? = null,
        custom_fields: List<CustomFieldFilter>? = null,
        onSuccess: (List<Task>) -> Unit = {},
    ): Response<List<Task>> =
        inBackground(onSuccess) {
            logger.debug("getting tasks for team=${team.name}")
            tokenClient.caching<Named<List<Task>>>(TASKS(team.id), clickUpUrl / "team" / team.id / "task") {
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
                custom_fields?.forEach { parameter("custom_fields", it.serialize()) }
            }.value
        }

    suspend fun getTask(
        taskId: TaskID,
        onSuccess: (Task?) -> Unit = {},
    ): Response<Task?> =
        inBackground(onSuccess) {
            logger.debug("getting task $taskId")
            tokenClient.get(clickUpUrl / "task" / taskId).body()
        }

    suspend fun getSpaces(
        team: Team,
        archived: Boolean = false,
        onSuccess: (List<Space>) -> Unit = {},
    ): Response<List<Space>> =
        inBackground(onSuccess) {
            logger.debug("getting spaces for team=${team.name}")
            tokenClient.caching<Named<List<Space>>>(SPACES(team.id), clickUpUrl / "team" / team.id / "space") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getLists(
        space: Space,
        archived: Boolean = false,
        onSuccess: (List<TaskList>) -> Unit = {},
    ): Response<List<TaskList>> =
        inBackground(onSuccess) {
            logger.debug("getting lists for space=${space.name}")
            tokenClient.caching<Named<List<TaskList>>>(SPACE_LISTS(space.id), clickUpUrl / "space" / space.id / "list") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getFolders(
        space: Space,
        archived: Boolean = false,
        onSuccess: (List<Folder>) -> Unit = {},
    ): Response<List<Folder>> =
        inBackground(onSuccess) {
            logger.debug("getting folders for space=${space.name}")
            tokenClient.caching<Named<List<Folder>>>(FOLDERS(space.id), clickUpUrl / "space" / space.id / "folder") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getLists(
        folder: Folder,
        archived: Boolean = false,
        onSuccess: (List<TaskList>) -> Unit = {},
    ): Response<List<TaskList>> =
        inBackground(onSuccess) {
            logger.debug("getting lists for folder=${folder.name}")
            tokenClient.caching<Named<List<TaskList>>>(FOLDER_LISTS(folder.id), clickUpUrl / "folder" / folder.id / "list") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getTimeEntry(
        team: Team,
        timeEntryID: TimeEntryID,
        onSuccess: (TimeEntry?) -> Unit = {},
    ): Response<TimeEntry?> =
        inBackground(onSuccess) {
            logger.debug("getting time entry $timeEntryID for team=${team.name}")
            tokenClient.get(clickUpUrl / "team" / team.id / "time_entries" / timeEntryID).body<Named<TimeEntry?>>().value
        }

    suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
        onSuccess: (TimeEntry?) -> Unit = {},
    ): Response<TimeEntry?> =
        inBackground(onSuccess) {
            logger.debug("getting running time entry for team=${team.name} and assignee=${assignee?.username}")
            tokenClient.caching<Named<TimeEntry?>>(RUNNING_TIME_ENTRY(team.id), clickUpUrl / "team" / team.id / "time_entries" / "current") {
                parameter("assignee", assignee?.id?.stringValue)
            }.value
        }

    @Serializable
    data class StartTimeEntryRequest(
        val tid: TaskID?,
        val description: String?,
        val billable: Boolean,
        val tags: List<Tag>,
    )

    suspend fun startTimeEntry(
        team: Team,
        taskId: TaskID? = null,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
        onSuccess: (TimeEntry) -> Unit = {},
    ): Response<TimeEntry> =
        inBackground(onSuccess) {
            logger.debug("starting time entry of task=${taskId?.stringValue ?: "<no task>"} for team=${team.name}")
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "start") {
                contentType(ContentType.Application.Json)
                setBody(StartTimeEntryRequest(taskId, description, billable, tags.toList()))
            }.body<Named<TimeEntry>>().value
        }

    suspend fun stopTimeEntry(
        team: Team,
        onSuccess: (TimeEntry) -> Unit = {},
    ): Response<TimeEntry> =
        inBackground(onSuccess) {
            logger.debug("stopping time entry for team=${team.name}")
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "stop").body<Named<TimeEntry>>().value
        }

    suspend fun addTagsToTimeEntries(
        team: Team,
        timeEntryIDs: List<TimeEntryID>,
        tags: List<Tag>,
        onSuccess: (Unit) -> Unit = {},
    ): Response<Unit> =
        inBackground(onSuccess) {
            logger.debug("adding tags $tags to time entries $timeEntryIDs for team=${team.name}")
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "tags") {
                contentType(ContentType.Application.Json)
                setBody(AddTagsToTimeEntriesRequest(timeEntryIDs, tags))
            }
        }


    @Serializable
    private data class AddTagsToTimeEntriesRequest(
        @SerialName("time_entry_ids") val timeEntryIDs: List<TimeEntryID>,
        @SerialName("tags") val tags: List<Tag>,
    )
}
