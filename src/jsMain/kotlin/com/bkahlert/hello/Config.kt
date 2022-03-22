package com.bkahlert.hello

import com.clickup.api.rest.AccessToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsModule("./application.json")
@JsNonModule
private external val _config: dynamic

@JsModule("./application.dev.json")
@JsNonModule
private external val _devConfig: dynamic

@Serializable
data class Config(
    @SerialName("clickup") val clickup: ClickupConfig = ClickupConfig(),
)

@Serializable
data class ClickupConfig(
    @SerialName("access-token") val fallbackAccessToken: AccessToken? = null,
)

val AppConfig: Config by lazy {
    js("return JSON.stringify(Object.assign({}, _config, _devConfig));")
        .unsafeCast<String>().deserialize()
}
