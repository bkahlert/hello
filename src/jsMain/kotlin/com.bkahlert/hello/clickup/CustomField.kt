@file:UseSerializers(UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.rest.Identifier
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject

@Serializable
data class CustomField(
    @SerialName("id") val id: ID,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
    @SerialName("type_config") val typeConfig: JsonObject?,
    @SerialName("date_created") val dateCreated: String,
    @SerialName("hide_from_guests") val hideFromGuests: Boolean,
    @SerialName("required") val required: Boolean?,
) {
    @Serializable value class ID(override val id: String) : Identifier<String>
}
