package com.bkahlert.hello.components.applet.media

import com.bkahlert.hello.components.applet.Applet
import com.bkahlert.hello.components.applet.AppletEditor
import com.bkahlert.hello.components.applet.AspectRatio
import com.bkahlert.hello.components.applet.panel
import com.bkahlert.hello.fritz2.UriLens
import com.bkahlert.hello.fritz2.components.metadata.Metadata
import com.bkahlert.hello.fritz2.components.metadata.fetchMetadata
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.fritz2.orDefault
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.fritz2.selectField
import com.bkahlert.hello.fritz2.selectMultipleField
import com.bkahlert.hello.fritz2.switchField
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.placeholder
import dev.fritz2.core.required
import dev.fritz2.core.src
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.foundation.setInitialFocus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLIFrameElement
import kotlin.time.Duration.Companion.seconds

@Serializable
data class WebsiteApplet(
    override val id: String,
    override val title: String? = null,
    override val icon: Uri? = null,
    @SerialName("src") val src: Uri? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.stretch,
    @SerialName("allow") val allow: List<PermissionPolicy>? = null,
    @SerialName("allow-fullscreen") val allowFullscreen: Boolean? = null,
    @SerialName("sandbox") val sandbox: List<ContentSecurityPolicy>? = null,
) : Applet {
    override fun editor(isNew: Boolean): AppletEditor<*> = EmbedAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        val missing = listOf(::src).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            iframe("w-full h-full border-0") {
                src(src.toString())
                allow(allow)
                sandbox(sandbox)
            }
        }
    }

    companion object {
        public fun title(): Lens<WebsiteApplet, String> =
            WebsiteApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun src(): Lens<WebsiteApplet, String> =
            WebsiteApplet::src.lens({ it.src }, { p, v -> p.copy(src = v?.toUriOrNull()) }) + UriLens

        public fun aspectRatio(): Lens<WebsiteApplet, AspectRatio?> =
            WebsiteApplet::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })

        public fun allow(): Lens<WebsiteApplet, List<PermissionPolicy>> =
            WebsiteApplet::allow.lens({ it.allow }, { p, v -> p.copy(allow = v) }).orEmpty()

        public fun allowFullscreen(): Lens<WebsiteApplet, Boolean> =
            WebsiteApplet::allowFullscreen.lens({ it.allowFullscreen }, { p, v -> p.copy(allowFullscreen = v) }).orDefault(false)

        public fun sandbox(): Lens<WebsiteApplet, List<ContentSecurityPolicy>> =
            WebsiteApplet::sandbox.lens({ it.sandbox }, { p, v -> p.copy(sandbox = v) }).orEmpty()
    }
}


class EmbedAppletEditor(isNew: Boolean, applet: WebsiteApplet) : AppletEditor<WebsiteApplet>(isNew, applet) {

    override val autocomplete: EmittingHandler<Unit, Metadata> = handleAndEmit { applet ->
        val uri = applet.src?.takeIf { it.host.orEmpty().contains(".") }
        if (uri != null) {
            autocompleting.track {
                uri.fetchMetadata()?.let { metadata ->
                    emit(metadata)
                    applet.copy(
                        title = metadata.title,
                        icon = metadata.favicon?.toUriOrNull() ?: applet.icon,
                    )
                }
            } ?: applet
        } else {
            applet
        }
    }

    override fun RenderContext.renderFields() {
        inputField {
            value(map(WebsiteApplet.src()))
            inputLabel {
                +"Source"
                inputTextfield {
                    type("url")
                    placeholder("https://example.com")
                    required(true)
                    setInitialFocus()
                    focuss handledBy { domNode.select() }
                    changes.debounce(0.2.seconds) handledBy autocomplete
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            val store = map(WebsiteApplet.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("My favourite website")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        selectField(
            store = map(WebsiteApplet.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )

        selectMultipleField(
            store = map(WebsiteApplet.allow()),
            label = "Allow",
            itemTitle = PermissionPolicy::name,
        )
        switchField(
            store = map(WebsiteApplet.allowFullscreen()),
            label = "Allow fullscreen",
        )
        selectMultipleField(
            store = map(WebsiteApplet.sandbox()),
            label = "Sandbox",
            itemTitle = ContentSecurityPolicy::name,
        )
    }
}


/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Permissions-Policy#directives">Permissions Policy directives</a> */
enum class PermissionPolicy {
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
fun Tag<HTMLIFrameElement>.allow(values: List<PermissionPolicy>?) {
    attr("allow", values?.filterNot { it == PermissionPolicy.fullscreen }?.joinToString(" "))
    attr("allowfullscreen", values?.contains(PermissionPolicy.fullscreen))
}

/** Sets the `allow` and `allowfullscreen` attributes. */
fun Tag<HTMLIFrameElement>.allow(values: Flow<List<PermissionPolicy>?>) {
    attr("allow", values.map { it?.filterNot { it == PermissionPolicy.fullscreen }?.joinToString(" ") })
    attr("allowfullscreen", values.map { it?.contains(PermissionPolicy.fullscreen) })
}

/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Security-Policy/sandbox">Content Security Policy</a> */
enum class ContentSecurityPolicy {
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
fun Tag<HTMLIFrameElement>.sandbox(values: List<ContentSecurityPolicy>?) {
    attr("sandbox", values?.joinToString(" "))
}

/** Sets the `sandbox` attribute. */
fun Tag<HTMLIFrameElement>.sandbox(values: Flow<List<ContentSecurityPolicy>?>) {
    attr("sandbox", values.map { it?.joinToString(" ") })
}
