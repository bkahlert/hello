package com.clickup.api.rest

import com.bkahlert.hello.JsonSerializer
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.runtime.LocalStorage
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.web.http.div
import com.clickup.api.ClickupList
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.Tag
import com.clickup.api.Task
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import com.clickup.api.rest.ClickUpException.Companion.wrapOrNull
import com.clickup.api.rest.ClickupClient.Cache.RUNNING_TIME_ENTRY
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
            // TODO cache
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
    ): Either<T, Throwable> =
        try {
            val success = block()
            onSuccess(success)
            Either.Left(success)
        } catch (e: Exception) {
            console.error("error", e.message) // TODO
            onFailure(e)
            Either.Right(e)
        }

    private sealed class Cache(
        private val key: String,
    ) {
        object USER : Cache("clickup-user")
        object TEAMS : Cache("clickup-teams")
        data class RUNNING_TIME_ENTRY(val id: Team.ID) : Cache("clickup-running-time-entry-${id.stringValue}")
        data class TASKS(val id: Team.ID) : Cache("clickup-team-tasks-${id.stringValue}")

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

    suspend fun getUser(onSuccess: (User) -> Unit = {}): Either<User, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting user")
            tokenClient.caching<Named<User>>(USER, clickUpUrl / "user").value
        }

    suspend fun getTeams(onSuccess: (List<Team>) -> Unit = {}): Either<List<Team>, Throwable> =
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
        space_ids: List<Space.ID>? = null,
        project_ids: List<Folder.ID>? = null,
        list_ids: List<ClickupList.ID>? = null,
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
    ): Either<List<Task>, Throwable> =
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

    suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
        onSuccess: (TimeEntry?) -> Unit = {},
    ): Either<TimeEntry?, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting running time entry for team=${team.name} and assignee=${assignee?.username}")
            tokenClient.caching<Named<TimeEntry?>>(RUNNING_TIME_ENTRY(team.id), clickUpUrl / "team" / team.id / "time_entries" / "current") {
                parameter("assignee", assignee?.id?.stringValue)
            }.value
        }

    @Serializable
    data class StartTimeEntryRequest(
        val tid: Task.ID?,
        val description: String?,
        val billable: Boolean,
        val tags: List<Tag>,
    )

    suspend fun startTimeEntry(
        team: Team,
        taskId: Task.ID? = null,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
        onSuccess: (TimeEntry) -> Unit = {},
    ): Either<TimeEntry, Throwable> =
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
    ): Either<TimeEntry, Throwable> =
        inBackground(onSuccess) {
            logger.debug("stopping time entry for team=${team.name}")
            RUNNING_TIME_ENTRY(team.id).evict()
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "stop").body<Named<TimeEntry>>().value
        }
}
