package com.bkahlert.hello.clickup.ui

import com.bkahlert.hello.clickup.api.Identifier
import com.bkahlert.hello.clickup.api.Team
import com.bkahlert.hello.clickup.api.TeamID
import com.bkahlert.hello.clickup.api.rest.AccessToken
import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.clear
import com.bkahlert.kommons.dom.getSerializable
import com.bkahlert.kommons.dom.provideDelegate
import com.bkahlert.kommons.dom.setSerializable

/**
 * [Storage] for [ClickUpMenuViewModelImpl] settings that
 * delegates to the specified [storage] with keys prefixed with the specified [prefix].
 */
class ClickUpStorage(private val storage: Storage) {

    /** Access token to use. */
    var accessToken: AccessToken? by storage

    /** The most recent selections by the user. */
    val selections = Selections(storage.scoped("selection"))

    val cache = storage.scoped("cache")

    fun clear() {
        storage.clear()
    }
}

class Selections(private val storage: Storage) {
    operator fun get(team: Team): Selection = get(team.id)
    operator fun get(teamID: TeamID): Selection {
        val typedStringValues: List<String> = storage.getSerializable<List<String>>(teamID.stringValue) ?: emptyList()
        return typedStringValues.map { Identifier.of(it) }
    }

    operator fun set(team: Team, selection: Selection) = set(team.id, selection)
    operator fun set(teamID: TeamID, selection: Selection) {
        storage.setSerializable(teamID.stringValue, selection.map { it.typedStringValue })
    }
}

typealias Selection = List<Identifier<*>>
