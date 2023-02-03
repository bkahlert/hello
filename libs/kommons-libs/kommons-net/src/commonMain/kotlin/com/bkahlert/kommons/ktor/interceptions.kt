package com.bkahlert.kommons.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.Sender
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder

/**
 * Returns this [HttpClient] with the [HttpSend]
 * configured so that only the next request is
 * intercepted.
 *
 * Future requests aren't affected.
 */
public fun HttpClient.interceptOnce(
    block: suspend Sender.(HttpRequestBuilder) -> HttpClientCall,
): HttpClient = apply {
    var intercepted = false
    plugin(HttpSend).intercept { context ->
        if (!intercepted) {
            try {
                block(context)
            } finally {
                intercepted = true
            }
        } else {
            execute(context)
        }
    }
}
