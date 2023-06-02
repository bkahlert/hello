package com.bkahlert.hello.fritz2

/**
 * A [SyncStore] synced with the specified [storage].
 */
public class TestSyncStore<D>(
    public var storage: D,
) : RootSyncStore<D>(storage) {
    override suspend fun load(): D = storage
    override suspend fun sync(oldData: D, newData: D): D = newData.also { storage = it }
}
