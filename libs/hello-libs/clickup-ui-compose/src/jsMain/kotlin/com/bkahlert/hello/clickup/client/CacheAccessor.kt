package com.bkahlert.hello.clickup.client

import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.json.LenientJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

public class CacheAccessor(
    public val key: String,
    public val getter: (String) -> String?,
    public val setter: (String, String?) -> Unit,
    public val logger: ConsoleLogger,
) {
    public inline fun <reified T> load(): T? = getter(key)
        ?.runCatching { LenientJson.decodeFromString<T>(this)?.also { logger.debug("successfully loaded cached response for $key") } }
        ?.onFailure { logger.warn("failed to load cached response for $key", it) }
        ?.getOrNull()

    // TODO move to ClickupStorage
    public inline fun <reified T> save(value: T) {
        logger.debug("caching response for $key")
        kotlin.runCatching {
            setter(key, LenientJson.encodeToString(value))
            logger.debug("successfully cached response for $key")
        }.onFailure {
            logger.warn("failed to cache response for $key")
        }.getOrNull()
    }

    public fun evict() {
        setter(key, null)
        logger.debug("removed cache entry for $key")
    }
}
