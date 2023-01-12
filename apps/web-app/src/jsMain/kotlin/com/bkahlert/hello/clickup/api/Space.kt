@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Space(
    @SerialName("id") val id: SpaceID,
    @SerialName("name") val name: String,
    @SerialName("private") val private: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("multiple_assignees") val multipleAssignees: Boolean,
) {
    fun asPreview() = SpacePreview(
        id = id,
        name = name,
        access = true,
    )
}

@Serializable value class SpaceID(override val id: String) : Identifier<String>

@Serializable
data class SpacePreview(
    @SerialName("id") val id: SpaceID,
    @SerialName("name") val name: String?,
    @SerialName("access") val access: Boolean?,
)
