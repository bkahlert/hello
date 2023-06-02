package com.bkahlert.hello

import com.bkahlert.hello.app.props.PropsStore
import com.bkahlert.hello.app.session.SessionStore
import com.bkahlert.hello.app.user.User
import com.bkahlert.hello.applet.AppletRegistration
import com.bkahlert.hello.applet.AppletSerializer
import com.bkahlert.hello.applet.Applets
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.image.ImageApplet
import com.bkahlert.hello.applet.preview.FeaturePreview
import com.bkahlert.hello.applet.preview.FeaturePreviewApplet
import com.bkahlert.hello.applet.ssh.WsSshApplet
import com.bkahlert.hello.applet.video.VideoApplet
import com.bkahlert.hello.applet.website.WebsiteApplet
import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.loader
import com.bkahlert.hello.fritz2.scrollTo
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.dom.ObjectUri
import com.bkahlert.kommons.dom.download
import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Tag
import dev.fritz2.core.accept
import dev.fritz2.core.type
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.get

@JsModule("./nyancat.svg")
private external val NyanCatSrc: String

private val appletRegistration by lazy {
    AppletRegistration().apply {
        register<FeaturePreviewApplet>(
            "feature-preview",
            title = "Feature Preview",
            description = "Demo of a future feature",
            icon = SolidHeroIcons.star
        )
        register<ImageApplet>(
            "image",
            title = "Image",
            description = "Displays an image",
            icon = SolidHeroIcons.photo
        )
        register<VideoApplet>(
            "video",
            title = "Video",
            description = "Embeds a video",
            icon = SolidHeroIcons.video_camera
        )
        register<WebsiteApplet>(
            "website", "embed",
            title = "Website",
            description = "Embeds an external website",
            icon = SolidHeroIcons.window
        )
        register<WsSshApplet>(
            "ws-ssh",
            title = "SSH",
            description = """Connect to a SSH server via a <a href="https://github.com/bkahlert/ws-ssh">WS-SSH proxy</a>.""",
            icon = SolidHeroIcons.command_line,
        )
    }
}

private val appletListSerializer by lazy {
    ListSerializer(AppletSerializer(appletRegistration))
}

private val defaultApplets by lazy {
    buildList {
        add(
            ImageApplet(
                id = "nyan-cat",
                title = "Nyan Cat",
                src = NyanCatSrc.toUri(),
                aspectRatio = AspectRatio.video
            )
        )
        add(
            VideoApplet(
                id = "rick-astley",
                title = "Rick Astley",
                src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
            )
        )
        add(
            WebsiteApplet(
                id = "impossible-color",
                title = "Impossible color",
                src = Uri("https://en.wikipedia.org/wiki/Impossible_color"),
                aspectRatio = AspectRatio.stretch,
            )
        )
        FeaturePreview.values().mapTo(this) {
            FeaturePreviewApplet(
                id = "feature-preview-${it.name}",
                feature = it,
            )
        }
    }
}

fun Tag<Element>.app(
) {
    div("flex justify-center items-center w-full h-full") {
        loader("Loading ...", classes = "w-16 h-16 opacity-15 animate-ping")
    }
}

fun Tag<Element>.app(
    sessionStore: SessionStore,
    propsStore: PropsStore,
) {
    propsStore
        .mapByKeyOrDefault("applets", defaultApplets, appletListSerializer)
        .let { Applets(it, appletRegistration) }
        .render(this)
    propsControls(propsStore).className("app-item")
}

fun Tag<Element>.app(
    userStore: SessionStore,
    user: User,
    propsStore: PropsStore,
) {
    propsStore
        .mapByKeyOrDefault("applets", defaultApplets, appletListSerializer)
        .let { Applets(it, appletRegistration) }
        .render(this)
    propsControls(propsStore).className("app-item")
}

fun Tag<Element>.propsControls(
    props: PropsStore,
): HtmlTag<HTMLDivElement> = div {

    div { icon("mx-auto w-12 h-12 text-default dark:text-invert opacity-60", SolidHeroIcons.wrench_screwdriver) }

    val jsonFormat = LenientAndPrettyJson
    fun downloadProps(
        props: PropsStore,
    ) {
        ObjectUri(ContentType.Application.Json, jsonFormat.encodeToString(props.current)).download("props.json")
    }

    div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),_1fr))] gap-8 m-8 items-start") {
        button(
            OutlineHeroIcons.cloud_arrow_down,
            "Save settings",
            "Make a backup of your current settings by downloading a copy.",
            simple = true,
            inverted = true
        ).apply {
            clicks handledBy { downloadProps(props) }
        }

        button(
            OutlineHeroIcons.cloud_arrow_up,
            "Restore settings",
            "Restore your settings by uploading a previously made copy.",
            simple = true,
            inverted = true
        ).apply {
            className("relative")
            input("absolute inset-0 w-full h-full p-0 m-0 outline-none opacity-0 cursor-pointer") {
                accept("application/json")
                type("file")
                dragovers.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.add("animate-bounce") }
                dragleaves.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.remove("animate-bounce") }
                dragends.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.remove("animate-bounce") }
                changes.mapTarget<HTMLInputElement>().mapNotNull { it.files?.get(0) } handledBy props.import
            }
        }

        button(
            OutlineHeroIcons.x_circle,
            "Reset to defaults",
            "Downloads your current settings and removes them from here.",
            simple = true,
            inverted = true
        ).apply {
            className("border-red-500 border-2")
            clicks handledBy {
                downloadProps(props)
                props.update(emptyMap())
                domNode.scrollTo(top = 0)
            }
        }
    }
}
