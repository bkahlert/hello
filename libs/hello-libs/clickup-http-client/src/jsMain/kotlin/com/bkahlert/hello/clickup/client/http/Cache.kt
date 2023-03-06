package com.bkahlert.hello.clickup.client.http

import com.bkahlert.hello.clickup.model.FolderID
import com.bkahlert.hello.clickup.model.SpaceID
import com.bkahlert.hello.clickup.model.TeamID
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import com.bkahlert.kommons.js.ConsoleLogger

/**
 * Simple cache that uses the specified [storage]
 * to store cache entries.
 */
public class Cache(private val storage: Storage) {

    private val logger = ConsoleLogger("Cache")

    public fun clear() {
        storage.clear()
    }

    public fun forUser(): CacheAccessor =
        CacheAccessor("user", storage::get, storage::set, logger)

    public fun forTeams(): CacheAccessor =
        CacheAccessor("teams", storage::get, storage::set, logger)

    public fun forRunningTimeEntry(id: TeamID): CacheAccessor =
        CacheAccessor("running-time-entry-${id.stringValue}", storage::get, storage::set, logger)

    public fun forTasks(id: TeamID): CacheAccessor =
        CacheAccessor("team-tasks-${id.stringValue}", storage::get, storage::set, logger)

    public fun forSpaces(id: TeamID): CacheAccessor =
        CacheAccessor("team-spaces-${id.stringValue}", storage::get, storage::set, logger)

    public fun forSpaceLists(id: SpaceID): CacheAccessor =
        CacheAccessor("space-lists-${id.stringValue}", storage::get, storage::set, logger)

    public fun forFolders(id: SpaceID): CacheAccessor =
        CacheAccessor("space-folders-${id.stringValue}", storage::get, storage::set, logger)

    public fun forFolder(id: FolderID): CacheAccessor =
        CacheAccessor("folder-${id.stringValue}", storage::get, storage::set, logger)

    public fun forFolderLists(id: FolderID): CacheAccessor =
        CacheAccessor("folder-lists-${id.stringValue}", storage::get, storage::set, logger)
}
