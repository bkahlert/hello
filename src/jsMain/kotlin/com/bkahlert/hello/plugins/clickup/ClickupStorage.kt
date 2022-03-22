package com.bkahlert.hello.plugins.clickup

import com.bkahlert.kommons.runtime.getSerializable
import com.bkahlert.kommons.runtime.setSerializable
import com.clickup.api.Identifier
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.rest.AccessToken
import org.w3c.dom.Storage
import kotlin.reflect.KProperty

/**
 * [Storage] for [ClickupModel] settings that
 * delegates to the specified [storage] with keys prefixed with the specified [prefix].
 */
class ClickupStorage(private val storage: Storage, private val prefix: String = "clickup.") {
    private fun scopedKey(key: String) = "$prefix$key"

    /** Access token to use. */
    var accessToken: AccessToken? by "access-token"

    /** The most recent selections by the user. */
    val selections = Selections(storage) { scopedKey("selection.${it.stringValue}") }

    companion object {
        /**
         * Delegate to read values from the [Storage] backing the specified [clickupStorage]
         * by using `this` string as the key to read the value to be deserialized to [T].
         */
        private inline operator fun <reified T> String.getValue(clickupStorage: ClickupStorage, property: KProperty<*>): T? =
            clickupStorage.storage.getSerializable<T>(clickupStorage.scopedKey(this))

        /**
         * Delegate to write values to the [Storage] backing the specified [clickupStorage]
         * by using `this` string as the key to write the serialized value of type [T].
         */
        private inline operator fun <reified T> String.setValue(clickupStorage: ClickupStorage, property: KProperty<*>, value: T?) {
            clickupStorage.storage.setSerializable<T>(clickupStorage.scopedKey(this), value)
        }
    }
}

class Selections(private val storage: Storage, private val key: (Identifier<*>) -> String) {
    operator fun get(team: Team): Selection = get(team.id)
    operator fun get(teamID: TeamID): Selection {
        val typedStringValues: List<String> = storage.getSerializable<List<String>>(key(teamID)) ?: emptyList()
        return typedStringValues.map { Identifier.of(it) }
    }

    operator fun set(team: Team, selection: Selection) = set(team.id, selection)
    operator fun set(teamID: TeamID, selection: Selection) {
        storage.setSerializable(key(teamID), selection.map { it.typedStringValue })
    }
}

typealias Selection = List<Identifier<*>>
