package com.bkahlert.hello.plugins.clickup

import com.bkahlert.kommons.dom.ScopedStorage.Companion.scoped
import com.bkahlert.kommons.dom.Storage
import com.bkahlert.kommons.dom.getSerializable
import com.bkahlert.kommons.dom.provideDelegate
import com.bkahlert.kommons.dom.setSerializable
import com.clickup.api.Identifier
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.rest.AccessToken

/**
 * [Storage] for [ClickupModel] settings that
 * delegates to the specified [storage] with keys prefixed with the specified [prefix].
 */
class ClickupStorage(private val storage: Storage) {

    /** Access token to use. */
    @JsName("accessToken")
    var `access-token`: AccessToken? by storage

    /** The most recent selections by the user. */
    val selections = Selections(storage.scoped("selection"))
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
