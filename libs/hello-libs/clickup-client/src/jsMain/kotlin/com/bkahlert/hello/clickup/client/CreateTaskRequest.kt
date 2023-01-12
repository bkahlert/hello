package com.bkahlert.hello.clickup.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CreateTaskRequest(
    @SerialName("name") val name: String,
)
