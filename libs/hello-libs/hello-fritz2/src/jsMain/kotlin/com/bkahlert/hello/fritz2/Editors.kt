package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.js.trace
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Keys
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Shortcut
import dev.fritz2.core.Store
import dev.fritz2.core.Window
import dev.fritz2.core.WithDomNode
import dev.fritz2.core.WithJob
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.lensOf
import dev.fritz2.core.multiple
import dev.fritz2.core.render
import dev.fritz2.core.rows
import dev.fritz2.core.selected
import dev.fritz2.core.shortcutOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.core.value
import dev.fritz2.headless.components.modal
import dev.fritz2.headless.foundation.OpenClose
import dev.fritz2.validation.ValidatingStore
import dev.fritz2.validation.ValidationMessage
import dev.fritz2.validation.valid
import kotlinx.browser.document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

/**
 * Renders an `<input>`-based editor for the given [store].
 */
public fun RenderContext.inputEditor(
    classes: String? = null,
    store: ValidatingStore<String, Unit, ValidationMessage>,
    init: HtmlTag<HTMLInputElement>.() -> Unit = {},
): HtmlTag<HTMLInputElement> {
    val update = store.handle<Event> { old, event ->
        (event.currentTarget as? HTMLInputElement)?.value ?: old
    }

    return input(classes) {
        type("text")
        value(store.current)
        customValidity(store.errorMessage)
        keydowns.stopPropagation() // might be cancelled by surrounding data collection otherwise
        keyups handledBy update
        changes handledBy update
        init()
    }
}

/**
 * Renders an `<input>`-based editor for the given [store].
 */
public fun RenderContext.passwordEditor(
    classes: String? = null,
    store: ValidatingStore<String, Unit, ValidationMessage>,
    init: HtmlTag<HTMLInputElement>.() -> Unit = {},
): HtmlTag<HTMLInputElement> {
    val update = store.handle<Event> { old, event ->
        (event.currentTarget as? HTMLInputElement)?.value ?: old
    }

    return input(classes) {
        type("password")
        value(store.current)
        customValidity(store.errorMessage)
        keydowns.stopPropagation() // might be cancelled by surrounding data collection otherwise
        keyups handledBy update
        changes handledBy update
        init()
    }
}

/**
 * Renders an `<textfield>`-based editor for the given [store].
 */
public fun RenderContext.textFieldEditor(
    classes: String? = null,
    store: ValidatingStore<String, Unit, ValidationMessage>,
    init: HtmlTag<HTMLTextAreaElement>.() -> Unit = {},
): HtmlTag<HTMLTextAreaElement> {
    val update = store.handle<Event> { old, event ->
        (event.currentTarget as? HTMLTextAreaElement)?.value ?: old
    }

    return textarea(classes) {
        rows(6)
        +store.current
        customValidity(store.errorMessage)
        keydowns.stopPropagation() // might be cancelled by surrounding data collection otherwise
        keyups handledBy update
        changes handledBy update
        init()
    }
}

/**
 * Renders an `<select>`-based editor for the given [store].
 */
public fun <T> RenderContext.selectEditor(
    classes: String? = null,
    store: Store<T>,
    vararg options: T,
    valueProvider: (T) -> String = { it.toString() },
    render: HtmlTag<HTMLOptionElement>.(T) -> Unit = { +it.toString() },
    init: HtmlTag<HTMLSelectElement>.() -> Unit = {},
): HtmlTag<HTMLSelectElement> {
    val update = store.handle<Event> { old, event ->
        (event.currentTarget as? HTMLSelectElement)?.value?.let { value -> options.firstOrNull { valueProvider(it) == value } } ?: old
    }

    return select(classes) {
        options.forEach { option ->
            option {
                value(valueProvider(option))
                selected(store.data.map { valueProvider(it) == valueProvider(option) })
                render(option)
            }
        }
        changes handledBy update
        init()
    }
}

/**
 * Renders an `<select>`-based editor for the given [store].
 */
public inline fun <reified E : Enum<E>> RenderContext.selectEditor(
    classes: String? = null,
    store: Store<E>,
    vararg options: E = enumValues(),
    noinline render: HtmlTag<HTMLOptionElement>.(E) -> Unit = { +it.name },
    noinline init: HtmlTag<HTMLSelectElement>.() -> Unit = {},
): HtmlTag<HTMLSelectElement> = selectEditor(
    classes = classes,
    store = store,
    options = options,
    valueProvider = { it.name },
    render = render,
    init = init,
)


/**
 * Renders an `<select>`-based editor for the given [store].
 */
public fun <T> RenderContext.multiSelectEditor(
    classes: String? = null,
    store: Store<List<T>>,
    vararg options: T,
    valueProvider: (T) -> String = { it.toString() },
    render: HtmlTag<HTMLOptionElement>.(T) -> Unit = { +it.toString() },
    init: HtmlTag<HTMLSelectElement>.() -> Unit = {},
): HtmlTag<HTMLSelectElement> {
    val update = store.handle<Event> { old, event ->
        (event.currentTarget as? HTMLSelectElement)?.options?.asList()
            ?.mapNotNull { (it as? HTMLOptionElement)?.let { option -> option.value.takeIf { option.selected } } }
            ?.let { values -> options.filter { valueProvider(it) in values } }
            ?: old
    }

    return select(classes) {
        multiple(true)
        options.forEach { option ->
            option {
                value(valueProvider(option))
                selected(store.data.map { it.map(valueProvider) }.map { valueProvider(option) in it })
                render(option)
            }
        }
        changes handledBy update
        init()
    }
}


/**
 * Renders an `<select>`-based editor for the given [store].
 */
public inline fun <reified E : Enum<E>> RenderContext.multiSelectEditor(
    classes: String? = null,
    store: Store<List<E>>,
    vararg options: E = enumValues(),
    noinline render: HtmlTag<HTMLOptionElement>.(E) -> Unit = { +it.name },
    noinline init: HtmlTag<HTMLSelectElement>.() -> Unit = {},
): HtmlTag<HTMLSelectElement> = multiSelectEditor(
    classes = classes,
    store = store,
    options = options,
    valueProvider = { it.name },
    render = render,
    init = init,
)

/**
 * Renders an [Uri] editor for the given [store].
 */
public fun RenderContext.uriEditor(
    classes: String? = null,
    store: Store<Uri>,
    init: HtmlTag<HTMLInputElement>.() -> Unit = {},
): HtmlTag<HTMLInputElement> = inputEditor(classes, store.mapValidating(lensOf("uri", { it.toString() }, { _, v -> v.toUri() })), init)

public interface Editor<D> {
    public fun validate(value: D) {}

    public fun renderEditor(
        renderContext: RenderContext,
        store: Store<D>,
        contributeMessages: (Flow<List<ValidationMessage>>) -> Unit,
    )

    public fun edit(
        store: Store<D>,
        vararg actions: EditorAction<D>,
        targetElement: HTMLElement? = document.body,
    ) {
        val editing = store.mapValidating(lensOf("validation", { it }, { p, v -> validate(v); v }))

        val open: Store<Boolean> = storeOf(true)
        val performAction: WithJob.(EditorAction<D>) -> Unit = { action ->
            action.handle(editing.current)
            open.update(false)
        }

        openModal(
            title = actions.filterIsInstance<EditorAction.Save<*>>().firstOrNull()?.name ?: "Edit",
            open = open,
            targetElement = targetElement,
            shortcuts = actions.mapNotNull { action ->
                when (val shortcut = action.shortcut) {
                    null -> null
                    else -> Pair<Shortcut, suspend WithJob.() -> Unit>(shortcut) { performAction(action) }
                }
            }.toTypedArray(),
            onBlur = { actions.filterIsInstance<EditorAction.Cancel<D>>().firstOrNull()?.let { action -> performAction(action) } },
        ) {
            var allMessages: Flow<List<ValidationMessage>> = editing.messages
            renderEditor(this, editing) {
                allMessages = allMessages.combine(flow {
                    emit(emptyList()) // add initial empty message list to make sure the flow always emits
                    emitAll(it)
                }) { old, new -> old + new }
            }

            div("flex flex-col items-center sm:flex-row sm:justify-end gap-2 mt-6") {
                allMessages.render { messages ->
                    ul("text-red-500") {
                        messages.forEach { message -> li { +message.toString() } }
                    }
                }
            }
            div("flex flex-col items-center sm:flex-row sm:justify-end gap-2 mt-6") {
                actions.forEach { action ->
                    button(
                        classes(
                            "inline-flex items-center justify-center w-full sm:w-32 px-4 py-2",
                            "rounded box-shadow box-glass disabled:opacity-75 text-sm font-semibold",
                            "transition [&:not(:disabled):hover]:-translate-y-0.5 [&:not(:disabled):hover]:shadow-xl [&:not(:disabled):hover]:dark:shadow-2xl",
                        )
                    ) {
                        type("button")
                        +action.name
                        shortcut(action.shortcut)
                        disabled(action.disabled(allMessages))
                        clicks.map { action } handledBy { performAction(it) }
                    }
                }
            }
        }
    }
}

public sealed interface EditorAction<D> {
    public val name: String
    public val shortcut: Shortcut?
    public val disabled: (Flow<List<ValidationMessage>>) -> Flow<Boolean>
    public val handle: (D) -> Unit

    public data class Save<D>(
        override val name: String = "Save",
        override val shortcut: Shortcut = Keys.Meta + "s",
        override val disabled: (Flow<List<ValidationMessage>>) -> Flow<Boolean> = { !it.valid },
        override val handle: (D) -> Unit,
    ) : EditorAction<D>

    public data class Cancel<D>(
        override val name: String = "Cancel",
        override val shortcut: Shortcut = Keys.Escape,
        override val disabled: (Flow<List<ValidationMessage>>) -> Flow<Boolean> = { flowOf(false) },
        override val handle: (D) -> Unit = {},
    ) : EditorAction<D>

    public data class Delete<D>(
        override val name: String = "Delete",
        override val shortcut: Shortcut? = null,
        override val disabled: (Flow<List<ValidationMessage>>) -> Flow<Boolean> = { flowOf(false) },
        override val handle: (D) -> Unit,
    ) : EditorAction<D>

    public companion object {
        public val DefaultEditorActions: Array<EditorAction<Nothing>> = arrayOf(
            Save {},
            Cancel {},
            Delete {},
        )

        public fun <D> Create(
            name: String = "Create",
            shortcut: Shortcut = Keys.Meta + "s",
            disabled: (Flow<List<ValidationMessage>>) -> Flow<Boolean> = { !it.valid },
            handle: (D) -> Unit,
        ): Save<D> = Save(
            name = name,
            shortcut = shortcut,
            disabled = disabled,
            handle = handle,
        )
    }
}

public fun openModal(
    title: String,
    open: Store<Boolean>,
    targetElement: HTMLElement? = document.body,
    vararg shortcuts: Pair<Shortcut, suspend WithJob.() -> Unit>,
    onBlur: (suspend WithJob.() -> Unit)?,
    content: ContentBuilder,
) {
    render(targetElement, override = false) {
        modal {
            openState(open)
            modalPanel("w-full fixed z-30 inset-0 ") {
                this@modal.shortcuts.mapNotNull { (event, shortcut) ->
                    shortcuts.firstNotNullOfOrNull { (s, h) -> if (s == shortcut) event to h else null }
                } handledBy { (event, handler) ->
                    event.stopImmediatePropagation()
                    event.preventDefault()
                    handler()
                }

                div("flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0") {
                    modalOverlay("fixed inset-0 bg-white/50 dark:bg-black/50 opacity-100 transition-opacity backdrop-blur-md") {
                        transition(
                            "ease-out duration-300",
                            "opacity-0",
                            "opacity-100",
                            "ease-in duration-200",
                            "opacity-100",
                            "opacity-0"
                        )
                    }
                    /* <!-- This element is to trick the browser into centering the modal contents. --> */
                    span("hidden sm:inline-block sm:align-middle sm:h-screen") {
                        attr("aria-hidden", "true")
                        +" "
                    }
                    div(
                        classes(
                            "inline-block align-bottom sm:align-middle w-full sm:max-w-2xl px-6 py-10 sm:p-14",
                            "box-shadow box-glass",
                            "shadow-2xl transform transition-all",
                            "text-left overflow-hidden",
                        ),
                    ) {
                        transition(
                            "ease-out duration-300",
                            "opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95",
                            "opacity-100 translate-y-0 sm:scale-100",
                            "ease-in duration-200",
                            "opacity-100 translate-y-0 sm:scale-100",
                            "opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
                        )

                        if (onBlur != null) this@modal.outsideClicks(this).drop(1) handledBy {
                            it.stopImmediatePropagation()
                            it.preventDefault()
                            onBlur()
                        }

                        div("mt-3 text-center sm:mt-0 sm:text-left") {
                            modalTitle("mb-5", tag = RenderContext::h1) { +title }
                        }
                        content()
                    }
                }
            }
        }
    }
}

public val OpenClose.shortcuts: Flow<Pair<KeyboardEvent, Shortcut>>
    get() = openState.data.flatMapLatest { isOpen ->
        Window.keydowns.filter { isOpen }
    }.map { it to shortcutOf(it) }

public fun OpenClose.outsideClicks(element: WithDomNode<*>): Flow<MouseEvent> =
    openState.data.flatMapLatest { isOpen ->
        Window.clicks.filter { event ->
            isOpen && event.composedPath().none { it == element.domNode }
        }
    }

public val HtmlTag<Element>.shortcuts: Flow<Pair<KeyboardEvent, Shortcut>>
    get() = Window.keydowns.filter {
        (domNode == document.activeElement).trace("HAS FOCUS")
    }.map { it to shortcutOf(it) }

public val HtmlTag<Element>.outsideClicks: Flow<MouseEvent>
    get() = Window.clicks.filter { event ->
        domNode == document.activeElement && event.composedPath().none { it == this.domNode }
    }
