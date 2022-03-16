package com.clickup.api

import com.clickup.api.rest.Identifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
