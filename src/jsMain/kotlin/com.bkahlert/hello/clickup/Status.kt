package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.rest.Identifier
import com.bkahlert.kommons.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("id") val id: ID,
    @SerialName("status") val status: String,
    @SerialName("color") val color: Color,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("type") val type: String,
) {
    @Serializable value class ID(override val id: String) : Identifier<String>

    @Serializable
    data class Preview(
        @SerialName("status") val status: String,
        @SerialName("color") val color: Color,
        @SerialName("orderindex") val orderIndex: Int,
        @SerialName("type") val type: String,
    )
}
