package com.bkahlert.hello.props.data

import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.auth.Session
import com.bkahlert.kommons.dom.uri
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
import kotlinx.serialization.json.JsonElement

public class SessionPropsDataSource(
    private val session: Session.AuthorizedSession,
    private val endpoint: Uri,
) : PropsDataSource {

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

    private suspend fun <R> grouping(operation: String, block: suspend () -> R): R =
        console.grouping("${SessionPropsDataSource::class.simpleName}: $operation", block = block)

    override suspend fun getAll(): Props = grouping(this::getAll.name) {
        client.get("$endpoint").body()
    }

    override suspend fun get(id: String): JsonElement? = grouping(this::get.name) {
        val response = client.get("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }

    override suspend fun set(id: String, value: JsonElement): JsonElement = grouping(this::set.name) {
        client.patch("$endpoint/$id") {
            contentType(Application.Json)
            setBody(value)
        }.body()
    }

    override suspend fun remove(id: String): JsonElement? = grouping(this::remove.name) {
        val response = client.delete("$endpoint/$id")
        when (response.status) {
            HttpStatusCode.NoContent -> null
            else -> response.body()
        }
    }
}
