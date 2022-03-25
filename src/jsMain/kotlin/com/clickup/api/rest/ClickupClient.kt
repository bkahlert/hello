package com.clickup.api.rest

import com.bkahlert.hello.JsonSerializer
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.dom.iterator
import com.bkahlert.kommons.dom.remove
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.web.http.div
import com.bkahlert.kommons.web.http.url
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
import com.clickup.api.div
import com.clickup.api.rest.ClickUpException.Companion.wrapOrNull
import com.clickup.api.rest.ClickupClient.Cache.FOLDERS
import com.clickup.api.rest.ClickupClient.Cache.FOLDER_LISTS
import com.clickup.api.rest.ClickupClient.Cache.RUNNING_TIME_ENTRY
import com.clickup.api.rest.ClickupClient.Cache.SPACES
import com.clickup.api.rest.ClickupClient.Cache.SPACE_LISTS
import com.clickup.api.rest.ClickupClient.Cache.TASKS
import com.clickup.api.rest.ClickupClient.Cache.TEAMS
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
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Date

data class ClickupClient(
    private val accessToken: AccessToken,
) {
    val host = window.location.url.host
    val clickUpUrl =
        if (host == "localhost" || host.startsWith("10.100.14")) {
            Url("http://$host:8080/api/v2")
        } else {
            Url("https://api.clickup.com/api/v2")
        }

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

    private suspend fun <T> runLogging(
        name: String,
        block: suspend () -> T,
    ): Result<T> = kotlin.runCatching {
        logger.debug(name)
        block()
    }.onFailure {
        logger.error("ClickUp error occurred", it)
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

        inline fun <reified T> load(): T? = localStorage[key]
            ?.runCatching { deserialize<T>()?.also { logger.debug("successfully loaded cached response for $key") } }
            ?.onFailure { logger.warn("failed to load cached response for $key", it) }
            ?.getOrNull()

        // TODO move to ClickupStorage
        inline fun <reified T> save(value: T) {
            logger.debug("caching response for $key")
            kotlin.runCatching {
                localStorage[key] = value.serialize(pretty = false)
                logger.debug("successfully cached response for $key")
            }.onFailure {
                logger.warn("failed to cache response for $key")
            }.getOrNull()
        }

        fun evict() {
            localStorage.remove(key)
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

    fun clearCache() {
        // TODO move cache and make clearing simpler
        logger.debug("clearing cache")
        for ((key, _) in localStorage) {
            logger.debug("delete $key?")
            if (key.startsWith("clickup-")) {
                logger.debug("deleting $key")
                localStorage.remove(key)
            }
        }
    }

    suspend fun getUser(): Result<User> =
        runLogging("getting user") {
            tokenClient.caching<Named<User>>(Cache.USER, clickUpUrl / "user").value
        }

    suspend fun getTeams(): Result<List<Team>> =
        runLogging("getting teams") {
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
    ): Result<List<Task>> =
        runLogging("getting tasks for team=${team.name}") {
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
                custom_fields?.forEach { parameter("custom_fields", it.serialize(pretty = false)) }
            }.value
        }

    suspend fun getTask(
        taskId: TaskID,
    ): Result<Task?> =
        runLogging("getting task $taskId") {
            tokenClient.get(clickUpUrl / "task" / taskId).body()
        }

    suspend fun getSpaces(
        team: Team,
        archived: Boolean = false,
    ): Result<List<Space>> =
        runLogging("getting spaces for team=${team.name}") {
            tokenClient.caching<Named<List<Space>>>(SPACES(team.id), clickUpUrl / "team" / team.id / "space") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getLists(
        space: Space,
        archived: Boolean = false,
    ): Result<List<TaskList>> =
        runLogging("getting lists for space=${space.name}") {
            tokenClient.caching<Named<List<TaskList>>>(SPACE_LISTS(space.id), clickUpUrl / "space" / space.id / "list") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getFolders(
        space: Space,
        archived: Boolean = false,
    ): Result<List<Folder>> =
        runLogging("getting folders for space=${space.name}") {
            tokenClient.caching<Named<List<Folder>>>(FOLDERS(space.id), clickUpUrl / "space" / space.id / "folder") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getLists(
        folder: Folder,
        archived: Boolean = false,
    ): Result<List<TaskList>> =
        runLogging("getting lists for folder=${folder.name}") {
            tokenClient.caching<Named<List<TaskList>>>(FOLDER_LISTS(folder.id), clickUpUrl / "folder" / folder.id / "list") {
                parameter("archived", archived)
            }.value
        }

    suspend fun getTimeEntry(
        team: Team,
        timeEntryID: TimeEntryID,
    ): Result<TimeEntry?> =
        runLogging("getting time entry $timeEntryID for team=${team.name}") {
            tokenClient.get(clickUpUrl / "team" / team.id / "time_entries" / timeEntryID).body<Named<TimeEntry?>>().value
        }

    suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
    ): Result<TimeEntry?> =
        runLogging("getting running time entry for team=${team.name} and assignee=${assignee?.username}") {
            tokenClient.caching<Named<TimeEntry?>>(RUNNING_TIME_ENTRY(team.id), clickUpUrl / "team" / team.id / "time_entries" / "current") {
                parameter("assignee", assignee?.id?.stringValue)
            }.value
        }

    suspend fun startTimeEntry(
        team: Team,
        taskId: TaskID? = null,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
    ): Result<TimeEntry> =
        runLogging("starting time entry of task=${taskId?.stringValue ?: "<no task>"} for team=${team.name}") {
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "start") {
                contentType(ContentType.Application.Json)
                setBody(StartTimeEntryRequest(taskId, description, billable, tags.toList()))
            }.body<Named<TimeEntry>>().value
        }

    suspend fun stopTimeEntry(
        team: Team,
    ): Result<TimeEntry> =
        runLogging("stopping time entry for team=${team.name}") {
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "stop").body<Named<TimeEntry>>().value
        }

    suspend fun addTagsToTimeEntries(
        team: Team,
        timeEntryIDs: List<TimeEntryID>,
        tags: List<Tag>,
    ): Result<Unit> =
        runLogging("adding tags $tags to time entries $timeEntryIDs for team=${team.name}") {
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "tags") {
                contentType(ContentType.Application.Json)
                setBody(AddTagsToTimeEntriesRequest(timeEntryIDs, tags))
            }
        }
}
