package com.bkahlert.hello.clickup.client

import kotlinx.serialization.Serializable

@Serializable
public data class ErrorInfo(
    val err: String,
    val ECODE: String,
)
