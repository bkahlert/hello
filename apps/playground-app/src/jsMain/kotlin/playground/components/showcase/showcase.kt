@file:Suppress("RedundantVisibilityModifier")

package playground.components.showcase

import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensOf
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.disclosure
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import playground.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.icon
import playground.fritz2.not
import playground.tailwind.heroicons.SolidHeroIcons
import kotlin.time.Duration.Companion.seconds

private val logger by ConsoleLogging("Showcase")

fun RenderContext.showcase(
    name: String,
    simple: Boolean = false,
    warning: String? = null,
    classes: String? = null,
    content: ContentBuilder? = null,
) {
    val resetStore = object : RootStore<Boolean>(false) {
        init {
            data.drop(1) handledBy {
                if (it) {
                    logger.grouping("reset $name") {
                        delay(1.seconds)
                        update(false)
                    }
                }
            }
        }
    }

    disclosure(
        classes(
            "sm:rounded-xl",
            if (simple) "bg-hero-diagonal-lines" else "box-shadow box-color",
            classes,
        )
    ) {
        openState(resetStore.map(lensOf("s", { !it }, { _, x -> !x })))
        div(
            classes(
                "flex justify-between items-center gap-x-8",
                "sm:rounded-t-xl sm:last:rounded-b-xl",
                if (simple) "" else "dark:shadow-slate-900 shadow-md",
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
            disclosureButton("flex items-center gap-x-2 group") {
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

        disclosurePanel(if (simple) "" else "p-4 relative") {
            transition(
                opened,
                "transition duration-100 ease-out",
                "opacity-0 scale-y-95",
                "opacity-100 scale-y-100",
                "transition duration-100 ease-in",
                "opacity-100 scale-y-100",
                "opacity-0 scale-y-95"
            )

            opened.render {
                if (it) {
                    content?.invoke(this)
                }
            }
        }
    }
}

fun RenderContext.showcases(
    classes: String? = null,
    name: String,
    icon: Uri? = null,
    simple: Boolean = false,
    content: ContentBuilder? = null,
) {
    div(
        classes(
            "space-y-5 py-4 sm:px-4 sm:rounded-xl",
            if (simple) "" else "box-shadow box-glass", classes
        )
    ) {
        div("flex items-center justify-center sm:justify-start gap-x-2") {
            icon("shrink-0 w-6 h-6", icon ?: SolidHeroIcons.swatch)
            div("text-xl font-bold") { +name }
        }

        div("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4") {
            content?.invoke(this)
        }
    }
}

fun RenderContext.showcases(
    name: String,
    icon: Uri? = null,
    simple: Boolean = false,
    content: ContentBuilder? = null,
) {
    showcases(null, name, icon, simple, content)
}
