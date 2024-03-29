package com.bkahlert.hello.showcase

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.custom
import com.bkahlert.hello.fritz2.not
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.ScopeContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensOf
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.disclosure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@JsModule("@spectrum-web-components/split-view/sp-split-view.js")
private external val SplitView: dynamic

private fun RenderContext.splitView(
    baseClass: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    content: ContentBuilder<HTMLElement>? = null,
): Tag<HTMLElement> {
    SplitView
    return custom("sp-split-view", baseClass, id, scope, content ?: {})
}

private val logger by ConsoleLogging("hello.showcase")

public fun RenderContext.showcase(
    name: String,
    resizable: Boolean = true,
    warning: String? = null,
    classes: String? = null,
    resetDuration: Duration = .5.seconds,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    val resetStore = object : RootStore<Boolean>(false) {
        init {
            data.drop(1) handledBy {
                if (it) {
                    logger.grouping("reset $name") {
                        delay(resetDuration)
                        update(false)
                    }
                }
            }
        }
    }

    disclosure(
        classes(
            "sm:rounded-xl",
            "bg-white/10 border border-white/20",
            "grid grid-rows-[1fr_minmax(1px,100%)]",
            classes,
        )
    ) {
        openState(resetStore.map(lensOf("s", { !it }, { _, x -> !x })))
        div(
            classes(
                "flex justify-between items-center gap-x-8",
                "sm:rounded-t-xl sm:last:rounded-b-xl",
                "px-4 py-2",
            )
        ) {
            span("font-bold") { +name }
            if (warning != null) {
                span("flex items-center gap-x-2 font-semibold text-yellow-600 dark:text-yellow-400") {
                    icon("shrink-0 w-4 h-4", SolidHeroIcons.exclamation_circle)
                    span { +warning }
                }
            }
            disclosureButton("flex items-center gap-x-2 group aria-expanded:ring-0") {
                className(opened.map { if (it) "" else "cursor-not-allowed opacity-75" })
                type("button")
                disabled(!opened)
                icon(
                    "shrink-0 w-4 h-4",
                    SolidHeroIcons.arrow_path
                ) { className(opened.map { if (it) "transition group-hover:rotate-90" else "animate-spin" }) }
                span { +"Reset" }
                clicks.map { true } handledBy resetStore.update
            }
        }

        disclosurePanel("overflow-y-auto") {
            transition(
                opened,
                "transition duration-100 ease-out",
                "opacity-0 scale-y-95",
                "opacity-100 scale-y-100",
                "transition duration-100 ease-in",
                "opacity-100 scale-y-100",
                "opacity-0 scale-y-95",
            )

            if (resizable) {
                splitView("hover:rounded-lg hover:ring-1 hover:ring-slate-900/10") {
                    attr("resizable", true)
                    attr("primary-size", "100%")
                    opened.render(into = this) {
                        if (it) {
                            div { content?.invoke(this) }
                            div {}
                        }
                    }
                }
            } else {
                opened.render(into = this) {
                    if (it) {
                        div { content?.invoke(this) }
                    }
                }
            }
        }
    }
}

public fun RenderContext.showcases(
    classes: String? = null,
    name: String,
    icon: Uri? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    div(
        classes(
            "space-y-5 py-4 sm:px-4 sm:rounded-xl",
            "bg-white/10 border border-white/20",
            classes,
        )
    ) {
        div("flex items-center justify-center sm:justify-start gap-x-2") {
            icon("shrink-0 w-6 h-6", icon ?: SolidHeroIcons.swatch)
            div("text-xl font-bold") { +name }
        }

        div("grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-4") {
            content?.invoke(this)
        }
    }
}

public fun RenderContext.showcases(
    name: String,
    icon: Uri? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    showcases(null, name, icon, content)
}
