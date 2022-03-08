package com.bkahlert.hello.clickup

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.bkahlert.hello.Serializer
import com.bkahlert.kommons.runtime.LocalStorage
import com.bkahlert.kommons.web.http.invoke
import com.bkahlert.kommons.web.http.url
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.auth.Auth
import io.ktor.client.features.auth.providers.BearerTokens
import io.ktor.client.features.auth.providers.bearer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.formUrlEncode
import kotlinx.browser.window
import kotlinx.serialization.Serializable

private val token = ""

@Serializable
data class TokenInfo(
    val access_token: String,
)

@Serializable
data class Error(
    val err: String,
    val ECODE: String,
)

object ClickUpApiClient {

    private const val AUTHORIZATION_CODE_QUERY_KEY = "code"
    private const val AUTHORIZATION_CODE_STORAGE_KEY = "clickup.authorization-code"
    private const val ACCESS_TOKEN_STORAGE_KEY = "clickup.access-token"

    //    val clickUpUrl = "https://api.clickup.com/api"
    val clickUpUrl = "http://localhost:8080/api"

    private val parameters = Url(window.location.href).parameters

    private val errors = listOf("auth-error", "token-error").associateWith { parameters[it] }
        .filterValues { it != null }
        .onEach { (error, message) ->
            console.error(error, message)
            window.alert("$error: $message")
        }

    private val tokenClient by lazy {
        HttpClient(Js) {
            install(JsonFeature) {
                serializer = Serializer
            }
        }
    }

    val client by lazy {
        if (errors.isNotEmpty()) return@lazy null
        HttpClient(Js) {
            install(JsonFeature) {
                serializer = Serializer
            }
            install(Auth) {
                LocalStorage[ACCESS_TOKEN_STORAGE_KEY]?.also { accessToken ->
                    bearer {
                        loadTokens { BearerTokens(accessToken, accessToken) }
                        refreshTokens {
                            window.alert("Removing expired access token")
                            LocalStorage.remove(ACCESS_TOKEN_STORAGE_KEY)
                            throw IllegalStateException("ClickUp access token is no longer valid")
                        }
                    }
                } ?: LocalStorage[AUTHORIZATION_CODE_STORAGE_KEY]?.also { authorizationCode ->
                    bearer {
                        loadTokens {
                            val url = Url("$clickUpUrl/v2/oauth/token?" + Parameters.build {
                                append("client_id", "GN6516W1PG9IHB9E9O4ITESONC9SP8V7")
                                append("client_secret", "") // TODO
                                append("code", authorizationCode)
                            }.formUrlEncode())
                            console.info("getting OAuth token from", url)
                            tokenClient.post<TokenInfo>(url).let {
                                LocalStorage[ACCESS_TOKEN_STORAGE_KEY] = it.access_token
                                BearerTokens(it.access_token, it.access_token)
                            }
                        }
                        refreshTokens {
                            window.alert("Removing expired access token")
                            LocalStorage.remove(ACCESS_TOKEN_STORAGE_KEY)
                            throw IllegalStateException("ClickUp access token is no longer valid")
                        }
                    }
                } ?: parameters[AUTHORIZATION_CODE_QUERY_KEY]?.also { authorizationCode ->
                    LocalStorage[AUTHORIZATION_CODE_STORAGE_KEY] = authorizationCode
                    window.location.url {
                        parameters.remove(AUTHORIZATION_CODE_QUERY_KEY)
                    }.also {
                        // TODO test if redirected correctly
                        window.alert(it.toString())
                        console.info("redirecting to", it)
//                        window.location.url = it
                    }
                } ?: run {
                    Url("https://app.clickup.com/api?" + Parameters.build {
                        append("client_id", "GN6516W1PG9IHB9E9O4ITESONC9SP8V7")
                        append("redirect_uri", "http://localhost:8080")
                    }.formUrlEncode()).also {
                        console.info("getting authorization code from", it)
                        window.location.url = it
                    }
                }
            }
        }
    }

    val teams = mutableStateListOf<Team>()
    val user = mutableStateOf<User?>(null)

    suspend fun login() {
        console.info("logging in")
        client?.apply {
            teams.addAll(get<BoxedTeams>("$clickUpUrl/v2/team").teams.also {
                console.info("teams queried", it)
            })
            user.value = get<BoxedUser>("$clickUpUrl/v2/user").user.also {
                console.info("user queried", it)
            }
        }
    }

    suspend fun reactivate() {
        if (LocalStorage[ACCESS_TOKEN_STORAGE_KEY] != null && errors.isEmpty()) login()
    }
}
