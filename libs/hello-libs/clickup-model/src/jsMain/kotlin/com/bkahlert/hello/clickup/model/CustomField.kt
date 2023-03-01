package com.bkahlert.hello.clickup.model

import com.bkahlert.kommons.time.InstantAsEpochMilliseconds
import com.bkahlert.kommons.time.InstantAsEpochMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CustomField(
    @SerialName("id") val id: CustomFieldID,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
//    @SerialName("type_config") val typeConfig: JsonObject?,
    @SerialName("date_created") @Serializable(InstantAsEpochMillisecondsSerializer::class) val dateCreated: InstantAsEpochMilliseconds,
    @SerialName("hide_from_guests") val hideFromGuests: Boolean,
    @SerialName("required") val required: Boolean?,
)

@Serializable public value class CustomFieldID(override val id: String) : Identifier<String>
