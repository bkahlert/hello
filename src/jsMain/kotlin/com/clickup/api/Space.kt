@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.rest.Identifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Space(
    @SerialName("id") val id: ID,
    @SerialName("name") val name: String,
    @SerialName("private") val private: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("multiple_assignees") val multipleAssignees: Boolean,
) {
    @Serializable value class ID(override val id: String) : Identifier<String>

    @Serializable
    data class Preview(
        @SerialName("id") val id: ID,
        @SerialName("name") val name: String?,
        @SerialName("access") val access: Boolean?,
    )
}
