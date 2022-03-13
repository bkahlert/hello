package com.bkahlert.hello.clickup.rest

import com.bkahlert.hello.JsonSerializer
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.ClickupList
import com.bkahlert.hello.clickup.Space
import com.bkahlert.hello.clickup.Tag
import com.bkahlert.hello.clickup.Task
import com.bkahlert.hello.clickup.Task.ID
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.TimeEntry
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.rest.ClickUpException.Companion.wrapOrNull
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.Either
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.web.http.div
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
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

class ClickupClient(
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
            onFailure(e)
            Either.Right(e)
        }

    suspend fun getUser(onSuccess: (User) -> Unit = {}): Either<User, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting user")
            tokenClient.get(clickUpUrl / "user").body<Named<User>>().value
        }

    suspend fun getTeams(onSuccess: (List<Team>) -> Unit = {}): Either<List<Team>, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting teams")
            tokenClient.get(clickUpUrl / "team").body<Named<List<Team>>>().value
        }

    suspend fun getSpaces(team: Team, onSuccess: (List<Space>) -> Unit = {}): Either<List<Space>, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting spaces for team=${team.name}")
            tokenClient.get(clickUpUrl / "team" / team.id / "space").body<Named<List<Space>>>().value
        }

    // TODO get folders

    suspend fun getLists(space: Space, archived: Boolean = false, onSuccess: (List<ClickupList>) -> Unit = {}): Either<List<ClickupList>, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting lists for space=${space.name}")
            tokenClient.get(clickUpUrl / "space" / space.id / "list") {
                parameter("archived", archived)
            }.body<Named<List<ClickupList>>>().value
        }

    suspend fun getTasks(
        team: Team,
        page: Int? = null,
        order_by: String? = null,
        reverse: Boolean? = null,
        subtasks: Boolean? = null,
        space_ids: List<String>? = null,
        project_ids: List<String>? = null,
        list_ids: List<String>? = null,
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
        custom_task_ids: Boolean? = null,
        team_id: Int? = null,
        onSuccess: (List<Task>) -> Unit = {},
    ): Either<List<Task>, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting tasks for team=${team.name}")
            tokenClient.get(clickUpUrl / "team" / team.id / "task") {
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
                parameter("custom_task_ids", custom_task_ids)
                parameter("team_id", team_id)
                custom_fields?.forEach { parameter("custom_fields", it.serialize()) }
            }.body<Named<List<Task>>>().value
        }

    suspend fun getTasks(
        list: ClickupList,
        archived: Boolean = false,
        page: Int? = null,
        order_by: String? = null,
        reverse: Boolean? = null,
        subtasks: Boolean? = null,
        statuses: List<String>? = null,
        includeClosed: Boolean? = null,
        assignees: List<String>? = null,
        due_date_gt: Date? = null,
        due_date_lt: Date? = null,
        date_created_gt: Date? = null,
        date_created_lt: Date? = null,
        date_updated_gt: Date? = null,
        date_updated_lt: Date? = null,
        custom_fields: List<CustomFieldFilter>? = null,
        onSuccess: (List<ClickupList>) -> Unit = {},
    ): Either<List<ClickupList>, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting tasks for list=${list.name}")
            tokenClient.get(clickUpUrl / "list" / list.id / "task") {
                parameter("archived", archived)
                parameter("page", page)
                parameter("order_by", order_by)
                parameter("reverse", reverse)
                parameter("subtasks", subtasks)
                statuses?.forEach { parameter("statuses", it) }
                parameter("include_closed", includeClosed)
                parameter("assignees", assignees)
                assignees?.forEach { parameter("assignees", it) }
                parameter("due_date_gt", due_date_gt)
                parameter("due_date_lt", due_date_lt)
                parameter("date_created_gt", date_created_gt)
                parameter("date_created_lt", date_created_lt)
                parameter("date_updated_gt", date_updated_gt)
                parameter("date_updated_lt", date_updated_lt)
                custom_fields?.forEach { parameter("custom_fields", it.serialize()) }
            }.body<Named<List<ClickupList>>>().value
        }

    suspend fun getRunningTimeEntry(
        team: Team,
        assignee: User?,
        onSuccess: (TimeEntry?) -> Unit = {},
    ): Either<TimeEntry?, Throwable> =
        inBackground(onSuccess) {
            logger.debug("getting running time entry for team=${team.name} and assignee=${assignee?.username}")
            tokenClient.get(clickUpUrl / "team" / team.id / "time_entries" / "current") {
                parameter("assignee", assignee?.id?.stringValue)
            }.body<Named<TimeEntry?>>().value
        }

    @Serializable
    data class StartTimeEntryRequest(
        val tid: ID,
        val description: String?,
        val billable: Boolean,
        val tags: List<Tag>,
    )

    suspend fun startTimeEntry(
        team: Team,
        task: Task,
        description: String? = null,
        billable: Boolean = false,
        vararg tags: Tag,
        onSuccess: (TimeEntry) -> Unit = {},
    ): Either<TimeEntry, Throwable> =
        inBackground(onSuccess) {
            logger.debug("starting time entry of task=${task.name} for team=${team.name}")
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "start") {
                val body1 = StartTimeEntryRequest(task.id, description, billable, tags.toList())
                console.warn("PAYLOAD", body1)
                console.warn("PAYLOAD", body1.toString())
                contentType(ContentType.Application.Json)
                setBody(body1)
            }.body<Named<TimeEntry>>().value
        }

    suspend fun stopTimeEntry(
        team: Team,
        onSuccess: (TimeEntry) -> Unit = {},
    ): Either<TimeEntry, Throwable> =
        inBackground(onSuccess) {
            logger.debug("stopping time entry for team=${team.name}")
            tokenClient.post(clickUpUrl / "team" / team.id / "time_entries" / "stop").body<Named<TimeEntry>>().value
        }
}
