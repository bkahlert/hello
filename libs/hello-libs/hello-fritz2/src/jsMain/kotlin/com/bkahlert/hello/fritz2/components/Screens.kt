@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.components

import dev.fritz2.core.Id
import dev.fritz2.core.RenderContext
import dev.fritz2.core.ScopeContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.dataCollection
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.TagFactory
import dev.fritz2.headless.foundation.addComponentStructureInfo
import dev.fritz2.headless.foundation.attrIfNotSet
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollBehavior
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollMode
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollPosition
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import org.w3c.dom.CENTER
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollIntoViewOptions
import org.w3c.dom.ScrollLogicalPosition


public enum class ScreensType(
    public val classes: String,
) {
    Vertical("snap-panels-v"),
    VerticalFull("snap-panels-v-full"),
    Horizontal("snap-panels-h"),
    HorizontalFull("snap-panels-h-full"),
}

public fun RenderContext.screens(
    pages: Store<List<Page>>,
    selectedPage: Store<Page?> = storeOf(null),
    type: ScreensType = ScreensType.Vertical,
): Unit = screens(null, pages, selectedPage, type)

// selected: the element worked with
// active: the focussed element that can be selected
public fun RenderContext.screens(
    classes: String?,
    pages: Store<List<Page>>,
    selectedPage: Store<Page?> = storeOf(null),
    type: ScreensType = ScreensType.Vertical,
) {

    dataCollection<Page>(classes) {
        data(pages.data, Page::id)
        selection.single(selectedPage)

        dataCollectionItems(type.classes) {
            scrollIntoView(
                behavior = ScrollBehavior.smooth,
                mode = ScrollMode.always,
                vertical = ScrollPosition.center,
                horizontal = ScrollPosition.center,
            )
            attrIfNotSet("role", Aria.Role.navigation)
            items.renderEach(Page::id, into = this) { item ->
                dataCollectionItem(
                    item = item,
                    id = item.id,
                    classes = classes("overflow-y-auto"),
                ) {
                    attrIfNotSet("role", Aria.Role.main)
                    attr("data-screen-selected", selected.asString())
                    attr("data-screen-active", active.asString())

                    className(selected.combine(active) { s, a ->
                        classes(
                            if (s) "ring-4 ring-slate-500/25 ring-inset ring-offset-0"
                            else if (a) "ring-8 ring-slate-500/33 ring-inset ring-offset-0"
                            else "ring-none",
//                            if (s) "opacity-100"
//                            else if (a) "opacity-75" else "opacity-50"
//                            if (s) "opacity-100"
//                            else if (a) "opacity-75" else "opacity-50"
                        )
                    })

                    // Screen container
                    div(
                        classes(
                            "space-y-5 py-4 sm:px-4",
                        )
                    ) {
                        // Screen header
                        div(
                            classes(
//                                "z-10",
//                                "sticky top-0 left-0",
                                "flex items-center justify-center sm:justify-start gap-x-2",
                            )
                        ) {
                            icon("shrink-0 w-6 h-6", item.icon)
                            div("text-xl font-bold") { +item.label }
                        }

                        // Screen content
                        div {
                            className(selected.combine(active) { sel, act ->
                                classes(
//                                    "transition duration-300 ease-in",
//                                    if (act) "" else "",
//                                    if (sel) "scale-100" else "scale-75"
                                )
                            })
                            item.pageContent?.invoke(this)
                        }
                    }

                    selected.mapNotNull { if (it) domNode else null } handledBy {
                        it.scrollIntoView(
                            ScrollIntoViewOptions(
                                behavior = org.w3c.dom.ScrollBehavior.SMOOTH,
                                inline = ScrollLogicalPosition.CENTER,
                                block = ScrollLogicalPosition.CENTER,
                            )
                        )
                    }
                }
            }
        }
    }
}


@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
public annotation class ScreensDsl

@ScreensDsl
public class Screens<C : HTMLElement>(tag: Tag<C>, id: String?) : Tag<C> by tag {

    public val componentId: String by lazy { id ?: Id.next() }
    private var screenCount = 0

    @ScreensDsl
    public class Screen<C : HTMLElement, CS : HTMLElement>(
        private val screens: Screens<C>,
        tag: Tag<CS>,
        public val index: Int,
    ) : Tag<CS> by tag {
        public fun render() {
            attrIfNotSet("role", Aria.Role.main)
        }
    }

    public fun <CS : HTMLElement> RenderContext.screen(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        tag: TagFactory<Tag<CS>>,
        initialize: Screen<C, CS>.() -> Unit,
    ) {
        val index = screenCount++
        addComponentStructureInfo("snap-panel", this@screen.scope, this)
        tag(this, classes("snap-panel", classes), "$componentId-screen-$index", scope) {
            Screen(this@Screens, this, index).run {
                initialize()
                render()
            }
        }
    }

    public fun RenderContext.screen(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        initialize: Screen<C, HTMLDivElement>.() -> Unit,
    ): Unit = screen(classes, scope, RenderContext::div, initialize)
}

public fun RenderContext.verticalScreens(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    type: ScreensType = ScreensType.VerticalFull,
    initialize: Screens<HTMLDivElement>.() -> Unit,
): Tag<HTMLDivElement> {
    addComponentStructureInfo("verticalScreens", this@verticalScreens.scope, this)
    return div(classes(type.classes, classes), id, scope) {
        Screens(this, id).run {
            initialize()
        }
    }
}

public fun RenderContext.horizontalScreens(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    type: ScreensType = ScreensType.HorizontalFull,
    initialize: Screens<HTMLDivElement>.() -> Unit,
): Tag<HTMLDivElement> {
    addComponentStructureInfo("horizontalScreens", this@horizontalScreens.scope, this)
    return div(classes(type.classes, classes), id, scope) {
        Screens(this, id).run {
            initialize()
        }
    }
}
