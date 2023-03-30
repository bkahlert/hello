package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.Editor
import com.bkahlert.hello.fritz2.EditorAction
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.props.prop
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.mapValidating
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.dom.favicon
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.disabled
import dev.fritz2.core.href
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import dev.fritz2.core.title
import dev.fritz2.core.transition
import dev.fritz2.core.type
import dev.fritz2.headless.components.Menu
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import dev.fritz2.validation.ValidationMessage
import io.ktor.http.ContentType.Image
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@Serializable
data class QuickLink(
    val title: String,
    val href: String,
    val icon: Uri = SolidHeroIcons.bookmark,
    val color: String? = null,
    val id: String? = randomString(),
) {
    fun open() {
        kotlinx.browser.window.open(href)
    }

    fun render(tag: Tag<HTMLButtonElement>, classes: String? = null) = tag.apply {
        className(
            classes(
                "inline-flex justify-center",
                "text-white/95 dark:text-white/75",
                if (color != null) "" else "hover:box-glass",
                "focus:outline-none focus-visible:ring-4 focus-visible:ring-white focus-visible:ring-opacity-75",
                classes,
            )
        )
        clicks handledBy { open() }
        title(title)
        icon(classes("w-full h-full"), icon)
        if (color != null) {
            mouseovers handledBy { inlineStyle("color: $color") }
            mouseouts handledBy { inlineStyle("") }
        }
    }
}

class QuickLinks(val store: SyncStore<List<QuickLink>>) : NavItem, SyncStore<List<QuickLink>> by store {

    val create = handle<Pair<QuickLink?, Tag<HTMLElement>>> { links, (template, modalParent) ->
        QuickLinkEditor.edit(
            storeOf(template ?: QuickLink("", "")),
            EditorAction.Create { update(links + it) },
            EditorAction.Cancel(),
            targetElement = modalParent.domNode,
        )
        links
    }

    val add = handle<QuickLink> { curr, link ->
        curr + link
    }

    val rankUp = handle<QuickLink> { links, link ->
        links.move(link, -1)
    }
    val rankDown = handle<QuickLink> { links, link ->
        links.move(link, +1)
    }

    val edit = handle<Pair<QuickLink, Tag<HTMLElement>>> { links, (link, modalParent) ->
        QuickLinkEditor.edit(
            storeOf(link),
            EditorAction.Save { new -> update(links.map { if (it.id == new.id) new else it }) },
            EditorAction.Cancel(),
            EditorAction.Delete { update(links - link) },
            targetElement = modalParent.domNode,
        )
        links
    }

    val delete = handle<QuickLink> { links, link ->
        links - link
    }

    override val content: (RenderContext.(selection: Store<NavItem?>, placement: Placement) -> Unit)
        get() = { _, _ ->
            div(
                classes(
                    "inline-flex items-center justify-center",
                    "rounded box-shadow",
                )
            ) {
                val openState = storeOf<QuickLink?>(null)
                val modalParent = this
                data.renderEach { link ->
                    menu {
                        openState(openState.map(lensOf("link", { it == link }, { p, v -> if (v) p else null })))

                        menuButton("rounded") {
                            link.render(this, "shrink-0 w-11 h-11 px-2 py-3").apply {
                                contextmenus.preventDefault().map { link } handledBy openState.update
                                // TODO use https://stackoverflow.com/questions/12304012/preventing-default-context-menu-on-longpress-longclick-in-mobile-safari-ipad
                            }
                        }

                        menuItems(
                            classes(
                                "absolute left-0",
                                "w-48",
                                "rounded-md",
                                "box-shadow box-glass",
                                "focus:outline-none"
                            )
                        ) {
                            placement = Placement.bottomStart
                            distance = 5

                            transition(
                                opened,
                                "transition ease-out duration-100",
                                "opacity-0 scale-95",
                                "opacity-100 scale-100",
                                "transition ease-in duration-75",
                                "opacity-100 scale-100",
                                "opacity-0 scale-95",
                            )

                            div("px-1 py-1") {
                                div("flex") {
                                    menuItem("flex-1 rounded-md px-2 py-2 text-center font-medium sm:text-sm block", tag = RenderContext::a) {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "box-glass" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Open"
                                        href(link.href)
                                    }
                                    menuItem("rounded-md px-2 py-2 text-center text-red-600 dark:text-red-400 font-medium sm:text-sm") {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "box-glass" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Delete"
                                        selected.map { link } handledBy delete
                                    }
                                }
                                div("flex") {
                                    menuItem("flex-auto rounded-md px-2 py-2 font-medium sm:text-sm") {
                                        val disabled = data.map { it.firstOrNull() == link }
                                        disabled(disabled)
                                        className(active.combine(disabled) { a, d -> if (a && !d) "box-glass" else if (d) "opacity-50 cursor-default" else "" })
                                        icon("w-4 h-4 mx-auto", SolidHeroIcons.arrow_left)
                                        title("Move left")
                                        selected.map { link } handledBy rankUp
                                    }
                                    menuItem("flex-auto rounded-md px-2 py-2 text-center font-medium sm:text-sm") {
                                        className(active.combine(disabled) { a, d -> if (a && !d) "box-glass" else if (d) "opacity-50 cursor-default" else "" })
                                        +"Edit..."
                                        selected.map { link to modalParent } handledBy edit
                                    }
                                    menuItem("flex-auto rounded-md px-2 py-2 font-medium sm:text-sm") {
                                        val disabled = data.map { it.lastOrNull() == link }
                                        disabled(disabled)
                                        className(active.combine(disabled) { a, d -> if (a && !d) "box-glass" else if (d) "opacity-50 cursor-default" else "" })
                                        icon("w-4 h-4 mx-auto", SolidHeroIcons.arrow_right)
                                        title("Move right")
                                        selected.map { link } handledBy rankDown
                                    }
                                }
                            }
                        }
                    }
                }

                button {
                    className(
                        classes(
                            "rounded",
                            "shrink-0 w-11 h-11 px-2 py-3",
                            "inline-flex justify-center",
                            "text-white/95 dark:text-white/75",
                            "hover:box-glass",
                            "focus:outline-none focus-visible:ring-4 focus-visible:ring-white focus-visible:ring-opacity-75",
                        )
                    )
                    type("button")
                    clicks.map {
                        QuickLink(
                            title = kotlinx.browser.window.document.title,
                            href = kotlinx.browser.window.location.href,
                            icon = kotlinx.browser.window.document.favicon?.toUriOrNull() ?: SolidHeroIcons.bookmark,
                        ) to modalParent
                    } handledBy create
                    title("Create bookmark...")
                    icon(classes("w-full h-full"), SolidHeroIcons.plus)
                }
            }
        }

    override val collapsedContent: (Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, selection: Store<NavItem?>) -> Unit)
        get() = { renderContext, _ ->
            renderContext.div(
                classes(
                    "w-full",
                    "flex items-center justify-evenly",
                )
            ) {
                current.forEach { link ->
                    menuItem("flex-1", tag = RenderContext::div) {
                        button { link.render(this, "w-full grow shrink-0 w-12 h-12 px-2 py-3 ring-inset") }
                    }
                }
            }
        }

    override fun toString(): String {
        return "QuickLinks(links=$current)"
    }

    companion object {

        fun PropsStore.quickLinks(
            vararg initialLinks: QuickLink = DefaultQuickLinks,
        ): QuickLinks = QuickLinks(prop("quick-links", { initialLinks.asList() }, Json))

        val DefaultQuickLinks = arrayOf(
            QuickLink(
                "GitHub",
                "https://github.com/bkahlert",
                DataUri(
                    Image.SVG, """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path
                        d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/></svg>"""
                ),
                "#181717",
            ),
        )
    }
}


object QuickLinkEditor : Editor<QuickLink> {
    override fun renderEditor(
        renderContext: RenderContext,
        store: Store<QuickLink>,
        contributeMessages: (Flow<List<ValidationMessage>>) -> Unit,
    ) {
        renderContext.div("flex flex-col sm:flex-row gap-4 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Title"
                    inputEditor(null, store.mapValidating(lensOf("title", { it.title }, { p, v ->
                        require(v.isNotBlank()) { "Title must not be blank" }
                        p.copy(title = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Link"
                    inputEditor(null, store.mapValidating(lensOf("href", { it.href }, { p, v ->
                        require(v.isNotBlank()) { "Link must not be blank" }
                        p.copy(href = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Icon"
                    inputEditor(null, store.mapValidating(lensOf("icon", { it.icon.toString() }, { p, v ->
                        if (v.isBlank()) p.copy(icon = SolidHeroIcons.bookmark)
                        else p.copy(icon = requireNotNull(v.toUriOrNull()) { "Icon is no valid URI" })
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Color"
                    inputEditor(null, store.mapValidating(lensOf("color", { it.color ?: "" }, { p, v ->
                        if (v.isBlank()) p.copy(color = null)
                        else p.copy(color = requireNotNull(Color.parseOrNull(v)) { "Invalid color" }.toString())
                    })).also { contributeMessages(it.messages) })
                }
            }

            div { store.data.render { button { it.render(this, "w-16 h-16") } } }
        }
    }
}

fun <T> List<T>.move(index: Int, offset: Int): List<T> {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index out of bounds: $index")
    val newIndex = (index + offset).coerceIn(indices)
    return toMutableList().apply { add(newIndex, removeAt(index)) }
}

fun <T> List<T>.move(element: T, offset: Int): List<T> =
    indexOfFirst { it == element }
        .takeUnless { it < 0 }
        ?.let { move(it, offset) }
        ?: this
