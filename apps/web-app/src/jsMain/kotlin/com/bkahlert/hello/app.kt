package com.bkahlert.hello

import com.bkahlert.hello.app.props.PropsStore
import com.bkahlert.hello.app.session.SessionStore
import com.bkahlert.hello.app.user.User
import com.bkahlert.hello.app.widgets.DefaultWidgetRegistration
import com.bkahlert.hello.app.widgets.DefaultWidgets
import com.bkahlert.hello.button.button
import com.bkahlert.hello.components.loader
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.scrollTo
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetRoute
import com.bkahlert.hello.widget.WidgetRouter
import com.bkahlert.hello.widget.Widgets
import com.bkahlert.hello.widget.serializer
import com.bkahlert.kommons.dom.ObjectUri
import com.bkahlert.kommons.dom.download
import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.json.LenientAndPrettyJson
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.accept
import dev.fritz2.core.lensOf
import dev.fritz2.core.type
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.get

fun Tag<Element>.app(
) {
    div("flex justify-center items-center w-full h-full") {
        loader("Loading ...", classes = "w-16 h-16 opacity-15 animate-ping")
    }
}

private fun widgets(store: SyncStore<List<Widget>>): Widgets {

    val router = WidgetRouter(store)

    val selectionState: Store<Widget?> = router.map(
        lensOf(
            id = "",
            getter = { it?.widget },
            setter = { p, v -> v?.let { WidgetRoute.Current(v, p?.edit ?: false) } },
        )
    )

    val editState: Store<Boolean> = router.map(
        lensOf(
            id = "",
            getter = { it?.edit == true },
            setter = { p, v -> p?.let { WidgetRoute.Current(it.widget, v) } },
        )
    )

    return Widgets(store, DefaultWidgetRegistration, selectionState, editState)
}

fun Tag<Element>.app(
    sessionStore: SessionStore,
    propsStore: PropsStore,
) {
    propsStore
        .mapByKeyOrDefault("widgets", DefaultWidgets, DefaultWidgetRegistration.serializer())
        .let { widgets(it) }
        .render(this)
    propsControls(propsStore).className("app-item")
}

fun Tag<Element>.app(
    userStore: SessionStore,
    user: User,
    propsStore: PropsStore,
) {
    propsStore
        .mapByKeyOrDefault("widgets", DefaultWidgets, DefaultWidgetRegistration.serializer())
        .let { widgets(it) }
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
            "Export settings",
            "Make a backup of your current settings by downloading a copy.",
            simple = true,
            inverted = true
        ).apply {
            clicks handledBy { downloadProps(props) }
        }

        button(
            OutlineHeroIcons.cloud_arrow_up,
            "Import settings",
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
