package com.bkahlert.hello.clickup.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class UpdateTaskRequest(
    @SerialName("status") val status: String?,
)
