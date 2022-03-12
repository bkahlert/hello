package com.bkahlert.hello.clickup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Space(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("private") val private: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("multiple_assignees") val multipleAssignees: Boolean,
) {

    @Serializable
    data class Preview(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String?,
        @SerialName("access") val access: Boolean?,
    )
}
