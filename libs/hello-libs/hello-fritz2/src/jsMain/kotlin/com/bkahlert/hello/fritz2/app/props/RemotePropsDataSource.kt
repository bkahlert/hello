package com.bkahlert.hello.fritz2.app.props

import com.bkahlert.hello.fritz2.app.env.Environment
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.oauth.API
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.resolve
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.browser.window
import kotlinx.serialization.json.JsonElement

public class RemotePropsDataSource(
    private val endpoint: Uri,
    private val client: HttpClient,
) : PropsDataSource {
    private val logger by ConsoleLogging

    override suspend fun get(): Map<String, JsonElement> = logger.grouping(::get) {
        client.get("$endpoint").body()
    }

    override suspend fun get(id: String): JsonElement? = logger.grouping(::get, id) {
        val response = client.get("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }

    override suspend fun set(id: String, value: JsonElement): JsonElement = logger.grouping(::set, id, value) {
        client.patch("$endpoint/$id") {
            contentType(Application.Json)
            setBody(value)
        }.body()
    }

    override suspend fun remove(id: String): JsonElement? = logger.grouping(::remove, id) {
        val response = client.delete("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }

    public companion object {
        private val clients = mutableMapOf<Pair<Uri, String>, RemotePropsDataSource>()

        public fun from(
            endpoint: Uri,
            session: Session.AuthorizedSession,
        ): RemotePropsDataSource = clients.getOrPut(endpoint to session.userInfo.subjectIdentifier) {
            RemotePropsDataSource(
                endpoint,
                JsonHttpClient {
                    session.installAuth(this, API("Props API", "props.hello.bkahlert.com", endpoint))
                }
            )
        }

        public fun from(
            environment: Environment,
            session: Session.AuthorizedSession,
        ): RemotePropsDataSource = from(
            endpoint = window.location.uri.resolve(environment.search(label = "Props Endpoint", keySubstring = "PROPS_API")),
            session = session,
        )
    }
}
