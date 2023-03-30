package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.mapValidating
import com.bkahlert.hello.fritz2.multiSelectEditor
import com.bkahlert.hello.fritz2.selectEditor
import com.bkahlert.hello.fritz2.uriEditor
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.takeUnlessEmpty
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.lensOf
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.validation.ValidationMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.HTMLIFrameElement

@Serializable
@SerialName("embed")
data class EmbedApplet(
    override val id: String = randomString(),
    override val name: String,
    @SerialName("src") val src: Uri,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio = AspectRatio.none,
    @SerialName("allow") val allow: List<PermissionPolicy>? = null,
    @SerialName("allow-fullscreen") val allowFullscreen: Boolean? = null,
    @SerialName("sandbox") val sandbox: List<ContentSecurityPolicy>? = null,
) : Applet {
    override val icon: Uri get() = EmbedApplet.icon

    override fun duplicate(): Applet = copy(id = randomString())

    override fun render(renderContext: RenderContext) {
        renderContext.window(name, aspectRatio) {
            iframe("w-full h-full border-0") {
                src(this@EmbedApplet.src.toString())
                allow(allow)
                sandbox(sandbox)
            }
        }
    }

    override fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet> {
        val store = storeOf(this)
        renderContext.div("flex flex-col sm:flex-row gap-8 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Name"
                    inputEditor(null, store.mapValidating(lensOf("name", { it.name }, { p, v ->
                        require(v.isNotBlank()) { "Name must not be blank" }
                        p.copy(name = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Source"
                    uriEditor(null, store.map(lensOf("src", { it.src }, { p, v -> p.copy(src = v) })))
                }
                label {
                    +"Aspect ratio"
                    selectEditor(
                        null, store.map(
                            lensOf(
                                "aspect-ratio",
                                { it.aspectRatio },
                                { p, v -> p.copy(aspectRatio = v) })
                        )
                    )
                }
                label {
                    +"Allow"
                    multiSelectEditor(
                        null, store.map(
                            lensOf(
                                "allow",
                                { it.allow.orEmpty() },
                                { p, v -> p.copy(allow = v.takeUnlessEmpty()) })
                        )
                    )
                }
                label {
                    +"Sandbox"
                    multiSelectEditor(
                        null, store.map(
                            lensOf(
                                "sandbox",
                                { it.sandbox.orEmpty() },
                                { p, v -> p.copy(sandbox = v.takeUnlessEmpty()) })
                        )
                    )
                }
            }
        }
        return store.data
    }

    companion object : AppletType<EmbedApplet> {
        override val name: String = "Embed"
        override val description: String = "Embeds an external website"
        override val icon: Uri = SolidHeroIcons.window
        override val default = EmbedApplet(
            name = "Rick Astley",
            src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
            aspectRatio = AspectRatio.video,
            allow = listOf(
                PermissionPolicy.accelerometer,
                PermissionPolicy.autoplay,
                PermissionPolicy.`clipboard-write`,
                PermissionPolicy.`encrypted-media`,
                PermissionPolicy.gyroscope,
                PermissionPolicy.`picture-in-picture`,
            ),
            allowFullscreen = true,
        )
    }
}


/** @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Permissions-Policy#directives">Permissions Policy directives</a> */
enum class PermissionPolicy {
    accelerometer,
    @JsName("ambient_light_sensor") `ambient-light-sensor`, autoplay,
    battery,
    camera,
    @JsName("clipboard_write") `clipboard-write`,
    @JsName("display_capture") `display-capture`,
    @JsName("document_domain") `document-domain`,
    @JsName("enc_media") `encrypted-media`, @JsName("ex_rendered") `execution-while-not-rendered`, @JsName("ex_viewport") `execution-while-out-of-viewport`,
    fullscreen,
    gamepad, geolocation, gyroscope,
    hid,
    @JsName("id_creds_get") `identity-credentials-get`,
    @JsName("idle_detection") `idle-detection`,
    @JsName("local_fonts") `local-fonts`,
    magnetometer, microphone, midi,
    payment,
    @JsName("pip") `picture-in-picture`, @JsName("pubkey_creds_get") `publickey-credentials-get`,
    @JsName("screen_wake_lock") `screen-wake-lock`, serial,
    @JsName("speaker_detection") `speaker-selection`,
    usb,
    @JsName("web_share") `web-share`,
    @JsName("xr_spatial_tracking") `xr-spatial-tracking`
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
    @JsName("allow_downloads") `allow-downloads`,

    //    @JsName("allow_downloads_without_user_activation") `allow-downloads-without-user-activation`,
    @JsName("allow_forms") `allow-forms`,
    @JsName("allow_modals") `allow-modals`,
    @JsName("allow_orientation_lock") `allow-orientation-lock`,
    @JsName("allow_pointer_lock") `allow-pointer-lock`,
    @JsName("allow_popups") `allow-popups`,
    @JsName("allow_popups_to_escape_sandbox") `allow-popups-to-escape-sandbox`,
    @JsName("allow_presentation") `allow-presentation`,
    @JsName("allow_same_origin") `allow-same-origin`,
    @JsName("allow_scripts") `allow-scripts`,
    @JsName("allow_storage_access_by_user_activation") `allow-storage-access-by-user-activation`,
    @JsName("allow_top_navigation") `allow-top-navigation`,
    @JsName("allow_top_navigation_by_user_activation") `allow-top-navigation-by-user-activation`,
    @JsName("allow_top_navigation_to_custom_protocols") `allow-top-navigation-to-custom-protocols`,
}

/** Sets the `sandbox` attribute. */
fun Tag<HTMLIFrameElement>.sandbox(values: List<ContentSecurityPolicy>?) {
    attr("sandbox", values?.joinToString(" "))
}

/** Sets the `sandbox` attribute. */
fun Tag<HTMLIFrameElement>.sandbox(values: Flow<List<ContentSecurityPolicy>?>) {
    attr("sandbox", values.map { it?.joinToString(" ") })
}
