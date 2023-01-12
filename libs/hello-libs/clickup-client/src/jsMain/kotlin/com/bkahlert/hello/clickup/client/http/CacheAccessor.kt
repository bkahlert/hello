package com.bkahlert.hello.clickup.client.http

import com.bkahlert.kommons.json.deserialize
import com.bkahlert.kommons.json.serialize
import com.bkahlert.kommons.logging.InlineLogger

public class CacheAccessor(
    public val key: String,
    public val getter: (String) -> String?,
    public val setter: (String, String?) -> Unit,
    public val logger: InlineLogger,
) {
    public inline fun <reified T> load(): T? = getter(key)
        ?.runCatching { deserialize<T>()?.also { logger.debug("successfully loaded cached response for $key") } }
        ?.onFailure { logger.warn("failed to load cached response for $key", it) }
        ?.getOrNull()

    // TODO move to ClickupStorage
    public inline fun <reified T> save(value: T) {
        logger.debug("caching response for $key")
        kotlin.runCatching {
            setter(key, value.serialize(pretty = false))
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
