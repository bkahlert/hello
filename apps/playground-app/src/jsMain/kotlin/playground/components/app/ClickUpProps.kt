package playground.components.app

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.kommons.json.LenientJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

data class ClickUpProps(
    val apiToken: PersonalAccessToken?,
) {
    companion object {
        fun Flow<JsonObject?>.mapClickUpProps(): Flow<ClickUpProps?> = map { clickUpProps ->
            when (clickUpProps) {
                null -> null
                else -> when (val apiToken = clickUpProps["api-token"]) {
                    null -> ClickUpProps(null)
                    else -> ClickUpProps(LenientJson.decodeFromJsonElement(apiToken))
                }
            }
        }
    }
}
