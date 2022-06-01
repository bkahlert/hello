package com.bkahlert.hello.clickup.api.rest

import com.bkahlert.hello.SimpleLogger.Companion.simpleLogger
import com.bkahlert.hello.clickup.api.FolderID
import com.bkahlert.hello.clickup.api.SpaceID
import com.bkahlert.hello.clickup.api.TeamID
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear

/**
 * Simple cache that uses the specified [storage]
 * to store cache entries.
 */
class Cache(private val storage: Storage) {

    fun clear() {
        storage.clear()
    }

    fun forUser() = CacheAccessor("user", storage::get, storage::set)
    fun forTeams() = CacheAccessor("teams", storage::get, storage::set)
    fun forRunningTimeEntry(id: TeamID) = CacheAccessor("running-time-entry-${id.stringValue}", storage::get, storage::set)
    fun forTasks(id: TeamID) = CacheAccessor("team-tasks-${id.stringValue}", storage::get, storage::set)
    fun forSpaces(id: TeamID) = CacheAccessor("team-spaces-${id.stringValue}", storage::get, storage::set)
    fun forSpaceLists(id: SpaceID) = CacheAccessor("space-lists-${id.stringValue}", storage::get, storage::set)
    fun forFolders(id: SpaceID) = CacheAccessor("space-folders-${id.stringValue}", storage::get, storage::set)
    fun forFolder(id: FolderID) = CacheAccessor("folder-${id.stringValue}", storage::get, storage::set)
    fun forFolderLists(id: FolderID) = CacheAccessor("folder-lists-${id.stringValue}", storage::get, storage::set)

    companion object {
        val Logger = simpleLogger()
    }
}
