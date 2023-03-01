package com.bkahlert.hello.props.data

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.oauth.API
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.resolve
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.browser.window
import kotlinx.serialization.json.JsonObject

public class SessionPropsDataSource(
    private val session: Session.AuthorizedSession,
    private val endpoint: Uri,
) : PropsDataSource {
    private val logger by ConsoleLogging

    public constructor(
        session: Session.AuthorizedSession,
        environment: Environment,
    ) : this(
        session = session,
        endpoint = window.location.uri.resolve(environment.search(label = "Props Endpoint", keySubstring = "PROPS_API")),
    )

    private val client by lazy {
        JsonHttpClient {
            session.installAuth(this, API("Props API", "props.hello.bkahlert.com", endpoint))
        }
    }

    override suspend fun getAll(): Props = logger.grouping(::getAll) {
        client.get("$endpoint").body()
    }

    override suspend fun get(id: String): JsonObject? = logger.grouping(::get, id) {
        val response = client.get("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }

    override suspend fun set(id: String, value: JsonObject): JsonObject = logger.grouping(::set, id, value) {
        client.patch("$endpoint/$id") {
            contentType(Application.Json)
            setBody(value)
        }.body()
    }

    override suspend fun remove(id: String): JsonObject? = logger.grouping(::remove, id) {
        val response = client.delete("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }
}
