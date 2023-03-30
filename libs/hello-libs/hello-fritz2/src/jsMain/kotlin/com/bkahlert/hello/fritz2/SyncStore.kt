package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.grouping
import dev.fritz2.core.Id
import dev.fritz2.core.Lens
import dev.fritz2.core.RootStore
import dev.fritz2.core.SimpleHandler
import dev.fritz2.core.Store
import dev.fritz2.core.SubStore
import dev.fritz2.core.Tag
import dev.fritz2.core.Update
import dev.fritz2.core.lensOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet
import org.w3c.dom.Element

/**
 * A [Store] that syncs its [data] with some source,
 * and provides a [synced] flow with the already synced data.
 */
public interface SyncStore<D> : Store<D> {
    public val syncState: Flow<SyncState<D>>

    /** The data that has been synced. */
    public val synced: Flow<D?>
        get() = syncState.map {
            when (it) {
                is SyncState.Cached -> null
                is SyncState.Syncing -> it.outdated
                is SyncState.Synced -> it.data
            }
        }

    /** A flow of whether the [data] is synced. */
    public val isSynced: Flow<Boolean> get() = data.combine(synced) { data, synced -> data == synced }

    /** Creates a new [SyncStore] that contains data and their synchronization state derived by a given Lens. */
    override fun <X> map(lens: Lens<D, X>): SyncStore<X>
}

/**
 * Creates a new [Store] with its [data] being the parent's [data] their [syncState].
 */
public fun <D> SyncStore<D>.withSyncState(): Store<Pair<D, Flow<SyncState<D>>>> = map(lensOf(
    id = "with-sync-state",
    getter = { it to syncState },
    setter = { _, (value, _) -> value }
))


/** Creates a new [SyncStore] that contains data and their synchronization state derived by a given Lens. */
public fun <D, X> Store<Pair<D, Flow<SyncState<D>>>>.map(lens: Lens<D, X>): Store<Pair<X, Flow<SyncState<X>>>> =
    map(
        lensOf(
            id = lens.id,
            getter = { (parent, parentSyncState) -> lens.get(parent) to parentSyncState.map { it.map(lens) } },
            setter = { (parent, parentSyncState), (value, _) -> lens.set(parent, value) to parentSyncState },
        )
    )

public sealed interface SyncState<D> {

    public val name: String
    public fun <X> map(lens: Lens<D, X>): SyncState<X>

    public data class Cached<D>(val cached: D) : SyncState<D> {
        override val name: String get() = "cached"
        override fun <X> map(lens: Lens<D, X>): SyncState<X> = Cached(lens.get(cached))
    }

    public data class Syncing<D>(val outdated: D, val data: D) : SyncState<D> {
        override val name: String get() = "syncing"
        override fun <X> map(lens: Lens<D, X>): SyncState<X> {
            val outdated = lens.get(outdated)
            val data = lens.get(data)
            return when (outdated) {
                data -> Synced(data)
                else -> Syncing(outdated, data)
            }
        }
    }

    public data class Synced<D>(val data: D) : SyncState<D> {
        override val name: String get() = "synced"
        override fun <X> map(lens: Lens<D, X>): SyncState<X> = Synced(lens.get(data))
    }
}

public class SubSyncStore<P, D>(
    private val parent: SyncStore<P>,
    private val lens: Lens<P, D>,
) : SyncStore<D>, Store<D> by SubStore(parent, lens) {
    override val syncState: Flow<SyncState<D>> get() = parent.syncState.map { it.map(lens) }
    override fun <X> map(lens: Lens<D, X>): SyncStore<X> = SubSyncStore(this, lens)
}

/**
 * A [SyncStore] that syncs its [data] with some source,
 * and provides a [synced] flow with the already synced data.
 *
 * The synchronization is performed by apply the specified [sync] function
 * to the last known synced data and the new data.
 * For the first synchronization [load] is used to obtain the initial data.
 */
public open class RootSyncStore<D>(
    override val id: String = Id.next(),
    cached: D,
    private val load: suspend () -> D,
    private val sync: suspend (oldData: D, newData: D) -> D,
) : RootStore<D>(cached, id), SyncStore<D> {

    protected val logger: ConsoleLogger by lazy { ConsoleLogger("${this::class.simpleName}@$id") }

    private val _syncState: MutableStateFlow<SyncState<D>> = MutableStateFlow(SyncState.Cached(cached))
    override val syncState: Flow<SyncState<D>> = _syncState.asStateFlow()
    private suspend fun sync(data: D): D = _syncState.updateAndGet { state ->
        logger.grouping("sync", state::class.simpleName) {
            logger.debug("state", state)
            logger.debug("data", data)
            when (state) {
                is SyncState.Cached -> SyncState.Syncing(load(), data)
                is SyncState.Syncing -> {
                    val outdated = state.outdated
                    val synced = if (outdated == data) data else sync(outdated, data)
                    SyncState.Synced(synced)
                }

                is SyncState.Synced -> if (state.data == data) state else SyncState.Syncing(state.data, data)
            }
        }
    }.let {
        when (it) {
            is SyncState.Syncing -> sync(it.data)
            is SyncState.Synced -> it.data
            else -> error("Unexpected sync state: $it")
        }
    }

    override suspend fun enqueue(update: Update<D>) {
        super.enqueue { update(it).also { super.enqueue { updated -> sync(updated) } } }
    }

    override fun <X> map(lens: Lens<D, X>): SyncStore<X> = SubSyncStore(this, lens)

    public val init: SimpleHandler<Unit> = this.handle {
        load().also { _syncState.value = SyncState.Synced(it) }
    }

    init {
        init()
    }
}


/** Sets a `data-sync` attribute.*/
public fun Tag<Element>.syncState(value: SyncState<*>): Unit = data("sync", value.name)

/** Sets a `data-sync` attribute only if its [value] is not null. */
public fun Tag<Element>.syncState(value: SyncState<*>?): Unit = data("sync", value?.name)

/** Sets a `data-sync` attribute. */
public fun Tag<Element>.syncState(value: Flow<SyncState<*>>): Unit = data("sync", value.map { it.name })

/** Sets a `data-sync` attribute only for all non-null values of the flow. */
public fun Tag<Element>.syncState(value: Flow<SyncState<*>?>): Unit = data("sync", value.map { it?.name })
