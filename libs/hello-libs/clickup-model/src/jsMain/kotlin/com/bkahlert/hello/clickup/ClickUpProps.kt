package com.bkahlert.hello.clickup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
public data class ClickUpProps(
    @SerialName("api-token") public val apiToken: PersonalAccessToken?,
)

public val JsonElement.clickUpProps: ClickUpProps get() = Json.decodeFromJsonElement(this)
public val Map<String, JsonElement>.clickUpProps: ClickUpProps? get() = get("clickup")?.clickUpProps
