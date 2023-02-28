package com.bkahlert.hello

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.kommons.json.LenientJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

data class ClickUpProps(
    val apiToken: PersonalAccessToken?,
) {
    companion object {
        fun Flow<JsonElement?>.mapClickUpProps(): Flow<ClickUpProps?> = map { clickUpProps ->
            if (clickUpProps == null) null
            else when (val apiToken = (clickUpProps as? JsonObject)?.get("api-token")) {
                null -> ClickUpProps(null)
                else -> ClickUpProps(LenientJson.decodeFromJsonElement(apiToken))
            }
        }
    }
}
