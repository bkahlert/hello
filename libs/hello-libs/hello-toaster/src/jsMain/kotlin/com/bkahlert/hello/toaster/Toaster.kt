package com.bkahlert.hello.toaster

import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.string
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.headless.components.Toast
import dev.fritz2.headless.components.toastContainer
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLUListElement
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public open class Toaster<T>(
    public val containerName: String = "toast-container--${Random.string()}",
    public val defaultDuration: Duration = 10.seconds,
    public val containerClasses: String? = classes(
        "absolute top-0 pt-5 right-0 pr-5 z-10 flex flex-col items-end"
    ),
    public val toastClasses: String? = classes(
        "flex-shrink-0",
        "w-48 mt-2.5 ml-2.5 px-4 py-3",
        "rounded shadow-md bg-glass text-default dark:text-invert text-sm",
        "border border-transparent",
    ),
    public val closable: Boolean = true,
    public val render: Toast<HTMLLIElement>.(T) -> Unit,
) {
    private var toastCount = 0
    private fun nextToastId() = "toast--$containerName--${toastCount++}"

    public fun attach(
        renderContext: RenderContext,
        classes: String? = containerClasses,
    ): Tag<HTMLUListElement> =
        renderContext.toastContainer(
            name = containerName,
            classes = classes,
            id = containerName,
        )

    public fun toast(data: T, duration: Duration = defaultDuration) {
        dev.fritz2.headless.components.toast(
            containerName = containerName,
            duration = duration.inWholeMilliseconds,
            classes = classes("relative group", toastClasses),
            nextToastId()
        ) {
            if (closable) {
                div("absolute p-2.5 transform opacity-0 group-hover:opacity-100 -top-5 -left-5") {
                    button("p-0.5 group-hover:bg-slate-500/50 border border-slate-500/75 rounded-full focus:outline-none focus:ring-2 focus:ring-white") {
                        icon("w-4 h-4 stroke-2 text", OutlineHeroIcons.x_mark)
                        clicks handledBy close
                    }
                }
            }
            render(data)
        }
    }
}
