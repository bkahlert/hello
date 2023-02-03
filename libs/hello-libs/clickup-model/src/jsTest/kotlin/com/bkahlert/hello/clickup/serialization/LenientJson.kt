package com.bkahlert.hello.clickup.serialization

import kotlinx.serialization.json.Json

val LenientJson: Json by lazy {
    Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = false
    }
}
