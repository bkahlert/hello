package com.clickup.api.rest

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.dom.Storage
import com.clickup.api.FolderID
import com.clickup.api.SpaceID
import com.clickup.api.TeamID

/**
 * Simple cache that uses the specified [storage]
 * to store cache entries.
 */
class Cache(private val storage: Storage) {

    class Accessor(val key: String, val getter: (String) -> String?, val setter: (String, String?) -> Unit) {
        inline fun <reified T> load(): T? = getter(key)
            ?.runCatching { deserialize<T>()?.also { Logger.debug("successfully loaded cached response for $key") } }
            ?.onFailure { Logger.warn("failed to load cached response for $key", it) }
            ?.getOrNull()

        // TODO move to ClickupStorage
        inline fun <reified T> save(value: T) {
            Logger.debug("caching response for $key")
            kotlin.runCatching {
                setter(key, value.serialize(pretty = false))
                Logger.debug("successfully cached response for $key")
            }.onFailure {
                Logger.warn("failed to cache response for $key")
            }.getOrNull()
        }

        fun evict() {
            setter(key, null)
            Logger.debug("removed cache entry for $key")
        }
    }

    fun forUser() = Accessor("user", storage::get, storage::set)
    fun forTeams() = Accessor("teams", storage::get, storage::set)
    fun forRunningTimeEntry(id: TeamID) = Accessor("running-time-entry-${id.stringValue}", storage::get, storage::set)
    fun forTasks(id: TeamID) = Accessor("team-tasks-${id.stringValue}", storage::get, storage::set)
    fun forSpaces(id: TeamID) = Accessor("team-spaces-${id.stringValue}", storage::get, storage::set)
    fun forSpaceLists(id: SpaceID) = Accessor("space-lists-${id.stringValue}", storage::get, storage::set)
    fun forFolders(id: SpaceID) = Accessor("space-folders-${id.stringValue}", storage::get, storage::set)
    fun forFolderLists(id: FolderID) = Accessor("folder-lists-${id.stringValue}", storage::get, storage::set)

    companion object {
        val Logger = simpleLogger()
    }
}
