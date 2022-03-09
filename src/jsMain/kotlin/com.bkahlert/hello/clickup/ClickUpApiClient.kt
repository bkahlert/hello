package com.bkahlert.hello.clickup

import com.bkahlert.hello.SerializerJson
import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.ClickUpException.Companion.wrapOrNull
import com.bkahlert.kommons.Either
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable


class ClickUpException(
    error: ErrorInfo,
    cause: Throwable?,
) : IllegalStateException("[${error.ECODE}] ${error.err}", cause) {
    companion object {
        /**
         * Returns a business exception with this exception as its cause if applicable.
         */
        suspend fun Throwable.wrapOrNull(): ClickUpException? =
            (this as? ResponseException)?.let {
                kotlin.runCatching { ClickUpException(it.response.body(), it) }.getOrNull()
            }
    }
}

@Serializable
data class ErrorInfo(
    val err: String,
    val ECODE: String,
)

class ClickUpApiClient(
    private val accessTokenState: StateFlow<String?>,
) {
    //    val clickUpUrl = "https://api.clickup.com/api"
    val clickUpUrl = "http://localhost:8080/api"

    private val logger = simpleLogger()

    private val tokenClient by lazy {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json(SerializerJson)
            }
            HttpResponseValidator {
                handleResponseException { throw it.wrapOrNull() ?: it }
            }
            install("ClickUp-PersonalToken-Authorization") {
                plugin(HttpSend).intercept { context ->
                    accessTokenState.value?.also { accessToken ->
                        logger.info("setting ${HttpHeaders.Authorization} header")
                        context.headers[HttpHeaders.Authorization] = accessToken
                    }
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
        inBackground(onSuccess) { tokenClient.get("$clickUpUrl/v2/user").body<BoxedUser>().user }

    suspend fun getTeams(onSuccess: (List<Team>) -> Unit = {}): Either<List<Team>, Throwable> =
        inBackground(onSuccess) { tokenClient.get("$clickUpUrl/v2/team").body<BoxedTeams>().teams }
}
