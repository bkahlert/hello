package com.bkahlert.hello.widget.website

import dev.fritz2.core.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLIFrameElement

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/sandbox">Content Security Policy</a> */
public enum class ContentSecurityPolicy {
    @JsName("allow_downloads")
    `allow-downloads`,

    //    @JsName("allow_downloads_without_user_activation") `allow-downloads-without-user-activation`,
    @JsName("allow_forms")
    `allow-forms`,

    @JsName("allow_modals")
    `allow-modals`,

    @JsName("allow_orientation_lock")
    `allow-orientation-lock`,

    @JsName("allow_pointer_lock")
    `allow-pointer-lock`,

    @JsName("allow_popups")
    `allow-popups`,

    @JsName("allow_popups_to_escape_sandbox")
    `allow-popups-to-escape-sandbox`,

    @JsName("allow_presentation")
    `allow-presentation`,

    @JsName("allow_same_origin")
    `allow-same-origin`,

    @JsName("allow_scripts")
    `allow-scripts`,

    @JsName("allow_storage_access_by_user_activation")
    `allow-storage-access-by-user-activation`,

    @JsName("allow_top_navigation")
    `allow-top-navigation`,

    @JsName("allow_top_navigation_by_user_activation")
    `allow-top-navigation-by-user-activation`,

    @JsName("allow_top_navigation_to_custom_protocols")
    `allow-top-navigation-to-custom-protocols`,
}


/** Sets the `sandbox` attribute. */
public fun Tag<HTMLIFrameElement>.sandbox(values: List<ContentSecurityPolicy>?) {
    attr("sandbox", values?.joinToString(" "))
}

/** Sets the `sandbox` attribute. */
public fun Tag<HTMLIFrameElement>.sandbox(values: Flow<List<ContentSecurityPolicy>?>) {
    attr("sandbox", values.map { it?.joinToString(" ") })
}
