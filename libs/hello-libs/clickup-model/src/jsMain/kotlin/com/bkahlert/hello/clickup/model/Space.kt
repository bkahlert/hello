package com.bkahlert.hello.clickup.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Space(
    @SerialName("id") val id: SpaceID,
    @SerialName("name") val name: String,
    @SerialName("private") val private: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("multiple_assignees") val multipleAssignees: Boolean,
) {
    public fun asPreview(): SpacePreview = SpacePreview(
        id = id,
        name = name,
        access = true,
    )
}

@Serializable public value class SpaceID(override val id: String) : Identifier<String>

@Serializable
public data class SpacePreview(
    @SerialName("id") val id: SpaceID,
    @SerialName("name") val name: String?,
    @SerialName("access") val access: Boolean?,
)
