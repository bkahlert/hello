package com.bkahlert.kommons.ktor

import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import kotlinx.serialization.json.JsonNull
import kotlin.reflect.KClass

/**
 * A client plugin that transforms [HttpStatusCode.NoContent] responses
 * to behave like responses with the specified [NoContentAsPluginConfig.noContentResponse].
 *
 * ***Important:** only responses with a compatible requested type are transformed,
 * to avoid a likely [ClassCastException].
 * That is, if you request a [String] but configure
 * an [Int] as the [NoContentAsPluginConfig.noContentResponse],
 * no transformation takes place.*
 */
public val NoContentAs: ClientPlugin<NoContentAsPluginConfig> =
    createClientPlugin("NoContentAsJsonNull", ::NoContentAsPluginConfig) {
        val noContentResponse: Any = pluginConfig.noContentResponse
        transformResponseBody { response: HttpResponse, _: ByteReadChannel, requestedType: TypeInfo ->
            when {
                response.status != HttpStatusCode.NoContent -> null

                requestedType.kotlinType
                    ?.classifier
                    ?.let { it as? KClass<*> }
                    ?.isInstance(noContentResponse) != true -> null

                else -> noContentResponse
            }
        }
    }

/** [NoContentAs] plugin configuration. */
public class NoContentAsPluginConfig {
    /** The content to use if a response with [HttpStatusCode.NoContent] is encountered. */
    public var noContentResponse: Any = JsonNull
}
