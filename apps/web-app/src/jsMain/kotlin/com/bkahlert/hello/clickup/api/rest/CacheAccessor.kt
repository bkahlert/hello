package com.bkahlert.hello.clickup.api.rest

import com.bkahlert.kommons.json.deserialize
import com.bkahlert.kommons.json.serialize

class CacheAccessor(val key: String, val getter: (String) -> String?, val setter: (String, String?) -> Unit) {
    inline fun <reified T> load(): T? = getter(key)
        ?.runCatching { deserialize<T>()?.also { Cache.Logger.debug("successfully loaded cached response for $key") } }
        ?.onFailure { Cache.Logger.warn("failed to load cached response for $key", it) }
        ?.getOrNull()

    // TODO move to ClickupStorage
    inline fun <reified T> save(value: T) {
        Cache.Logger.debug("caching response for $key")
        kotlin.runCatching {
            setter(key, value.serialize(pretty = false))
            Cache.Logger.debug("successfully cached response for $key")
        }.onFailure {
            Cache.Logger.warn("failed to cache response for $key")
        }.getOrNull()
    }

    fun evict() {
        setter(key, null)
        Cache.Logger.debug("removed cache entry for $key")
    }
}
