package com.bkahlert.hello.fritz2

import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.transition
import dev.fritz2.headless.components.checkboxGroup
import dev.fritz2.headless.components.listbox
import dev.fritz2.headless.components.popOver
import dev.fritz2.headless.components.switchWithLabel
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

private fun Tag<HTMLButtonElement>.renderAsSwitch(enabled: Flow<Boolean>) {
    className(
        classes(
            "relative inline-flex h-6 w-11",
            "cursor-pointer rounded-full",
            "form-checkbox form-field border-2 bg-gray-200 text-gray-700",
            "transition-colors ease-in-out duration-200",
        )
    )
    span(
        classes(
            "inline-block h-5 w-5",
            "rounded-full border-2 border-transparent bg-gray-700 bg-clip-padding pointer-events-none",
            "transform transition ease-in-out duration-200",
        )
    ) {
        className(enabled.map { if (it) "translate-x-5" else "translate-x-0 bg-gray-700/60" })
        attr(Aria.hidden, "true")
    }
}

/** Renders a [switchWithLabel]-based `select` element. */
public fun RenderContext.switchField(
    store: Store<Boolean>,
    label: String,
): Tag<HTMLDivElement> = switchWithLabel {
    value(store)
    switchLabel("flex items-center justify-between py-2") {
        +label
        switchToggle {
            renderAsSwitch(enabled)
        }.also(::mergeValidationMessages)
    }
}

/** Renders a [listbox]-based `select` element. */
public fun <D> RenderContext.selectField(
    store: Store<D?>,
    label: String,
    options: List<D>,
    itemTitle: (D) -> String,
    itemIcon: (D) -> Uri? = { null },
): Tag<HTMLDivElement> = listbox<D?> {
    value(store)
    listboxLabel {
        +label
        listboxButton(
            classes(
                "form-select form-field",
                "flex items-center justify-start gap-2",
            )
        ) {
            value.data.render(this) { value ->
                value?.let(itemIcon)?.also { icon("stretch-0 w-5 h-5", it) }
                +(value?.let(itemTitle) ?: "—")
            }
        }.also(::mergeValidationMessages)
    }
    listboxItems("form-field flex flex-col", tag = RenderContext::ul) {
        placement = Placement.bottom
        transition(
            opened,
            "transition duration-100 ease-out",
            "opacity-0 scale-95",
            "opacity-100 scale-100",
            "transition duration-100 ease-in",
            "opacity-100 scale-100",
            "opacity-0 scale-95"
        )
        options.forEach { option ->
            listboxItem(
                option, classes(
                    "cursor-pointer select-none px-3 py-2",
                    "flex items-center justify-start gap-2",
                ), tag = RenderContext::li
            ) {
                className(active.combine(disabled) { a, d ->
                    if (a && !d) "bg-slate-500/20"
                    else if (d) "opacity-50 cursor-default" else ""
                })
                option.let(itemIcon)?.also { icon("stretch-0 w-5 h-5", it) }
                +option.let(itemTitle)
            }
        }
    }
}

/** Renders a [listbox]-based `select` element. */
public inline fun <reified E : Enum<E>> RenderContext.selectField(
    store: Store<E?>,
    label: String,
    noinline itemTitle: (E) -> String = { it.name },
    noinline itemIcon: (E) -> Uri? = { null },
): Tag<HTMLDivElement> = selectField(store, label, enumValues<E>().toList(), itemTitle, itemIcon)


/** Renders a [checkboxGroup]-based `select` element. */
public fun <D> RenderContext.selectMultipleField(
    store: Store<List<D>>,
    label: String,
    options: List<D>,
    itemTitle: (D) -> String,
    itemIcon: (D) -> Uri? = { null },
): Tag<HTMLDivElement> = checkboxGroup<D> {
    value(store)
    popOver {
        checkboxGroupLabel {
            +label
            popOverButton(
                classes(
                    "form-select form-field",
                    "flex items-center justify-start gap-2",
                )
            ) {
                value.data.render(this) { value ->
                    when (value.size) {
                        0 -> {}
                        1 -> icon("stretch-0 w-5 h-5", value.first().let(itemIcon) ?: OutlineHeroIcons.stop)
                        else -> icon("stretch-0 w-5 h-5", OutlineHeroIcons.square_2_stack)
                    }
                    +when (value.size) {
                        0 -> "—"
                        1 -> value.first().let(itemTitle)
                        else -> "${value.size} selected"
                    }
                }
            }.also(::mergeValidationMessages)
        }
        popOverPanel("form-field bg-white flex flex-col max-h-[80vh] overflow-y-auto", tag = RenderContext::ul) {
            clicks.stopPropagation().preventDefault()
            options.forEach { option ->
                checkboxGroupOption(option, tag = RenderContext::li) {
//                className(active.combine(disabled) { a, d ->
//                    if (a && !d) "bg-slate-500/20"
//                    else if (d) "opacity-50 cursor-default" else ""
//                })
                    checkboxGroupOptionToggle(
                        classes(
                            "cursor-pointer select-none px-3 py-2",
                            "flex items-center justify-between gap-2",
                        )
                    ) {
                        checkboxGroupOptionLabel("flex items-center justify-start gap-2", tag = RenderContext::span) {
                            option.let(itemIcon)?.also { icon("stretch-0 w-5 h-5", it) }
                            +option.let(itemTitle)
                        }
                        button {
                            renderAsSwitch(selected)
                        }
                    }
                }
            }
        }
    }
}

/** Renders a [checkboxGroup]-based `select` element. */
public inline fun <reified E : Enum<E>> RenderContext.selectMultipleField(
    store: Store<List<E>>,
    label: String,
    noinline itemTitle: (E) -> String = { it.name },
    noinline itemIcon: (E) -> Uri? = { null },
): Tag<HTMLDivElement> = selectMultipleField(store, label, enumValues<E>().toList(), itemTitle, itemIcon)
