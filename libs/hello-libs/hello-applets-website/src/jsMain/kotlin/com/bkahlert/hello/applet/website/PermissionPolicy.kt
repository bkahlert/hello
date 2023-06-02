package com.bkahlert.hello.applet.website

import dev.fritz2.core.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLIFrameElement

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Permissions-Policy#directives">Permissions Policy directives</a> */
public enum class PermissionPolicy {
    accelerometer,

    @JsName("ambient_light_sensor")
    `ambient-light-sensor`, autoplay,
    battery,
    camera,

    @JsName("clipboard_write")
    `clipboard-write`,

    @JsName("display_capture")
    `display-capture`,

    @JsName("document_domain")
    `document-domain`,

    @JsName("enc_media")
    `encrypted-media`,

    @JsName("ex_rendered")
    `execution-while-not-rendered`,

    @JsName("ex_viewport")
    `execution-while-out-of-viewport`,
    fullscreen,
    gamepad, geolocation, gyroscope,
    hid,

    @JsName("id_creds_get")
    `identity-credentials-get`,

    @JsName("idle_detection")
    `idle-detection`,

    @JsName("local_fonts")
    `local-fonts`,
    magnetometer, microphone, midi,
    payment,

    @JsName("pip")
    `picture-in-picture`,

    @JsName("pubkey_creds_get")
    `publickey-credentials-get`,

    @JsName("screen_wake_lock")
    `screen-wake-lock`, serial,

    @JsName("speaker_detection")
    `speaker-selection`,
    usb,

    @JsName("web_share")
    `web-share`,

    @JsName("xr_spatial_tracking")
    `xr-spatial-tracking`
}


/** Sets the `allow` and `allowfullscreen` attributes. */
public fun Tag<HTMLIFrameElement>.allow(values: List<PermissionPolicy>?) {
    attr("allow", values?.filterNot { it == PermissionPolicy.fullscreen }?.joinToString(" "))
    attr("allowfullscreen", values?.contains(PermissionPolicy.fullscreen))
}

/** Sets the `allow` and `allowfullscreen` attributes. */
public fun Tag<HTMLIFrameElement>.allow(values: Flow<List<PermissionPolicy>?>) {
    attr("allow", values.map { it?.filterNot { it == PermissionPolicy.fullscreen }?.joinToString(" ") })
    attr("allowfullscreen", values.map { it?.contains(PermissionPolicy.fullscreen) })
}
