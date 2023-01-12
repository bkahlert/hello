@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonObject

@Serializable
data class CustomField(
    @SerialName("id") val id: CustomFieldID,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
    @SerialName("type_config") val typeConfig: JsonObject?,
    @SerialName("date_created") val dateCreated: String,
    @SerialName("hide_from_guests") val hideFromGuests: Boolean,
    @SerialName("required") val required: Boolean?,
)

@Serializable value class CustomFieldID(override val id: String) : Identifier<String>
