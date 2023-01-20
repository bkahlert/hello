package com.bkahlert.hello.clickup.viewmodel

import com.bkahlert.hello.clickup.model.Identifier
import com.bkahlert.hello.clickup.model.Team
import com.bkahlert.hello.clickup.model.TeamID
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * [Storage] for [ClickUpMenuViewModelImpl] settings that
 * delegates to the specified [storage] with keys prefixed with the specified [prefix].
 */
public class ClickUpStorage(private val storage: Storage) {

    /** The most recent selections by the user. */
    public val selections: Selections = Selections(storage.scoped("selection"))

    public val cache: Storage = storage.scoped("cache")

    public fun clear() {
        storage.clear()
    }
}

public class Selections(private val storage: Storage) {
    public operator fun get(team: Team): Selection = get(team.id)
    public operator fun get(teamID: TeamID): Selection {
        val typedStringValues: List<String> = storage[teamID.stringValue]?.let { Json.decodeFromString(it) } ?: emptyList()
        return typedStringValues.map { Identifier.of(it) }
    }

    public operator fun set(team: Team, selection: Selection): Unit = set(team.id, selection)
    public operator fun set(teamID: TeamID, selection: Selection) {
        storage[teamID.stringValue] = selection.map { it.typedStringValue }.let { Json.encodeToString(it) }
    }
}

public typealias Selection = List<Identifier<*>>
