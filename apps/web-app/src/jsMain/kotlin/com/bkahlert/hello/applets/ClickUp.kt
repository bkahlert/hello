package com.bkahlert.hello.applets

import com.bkahlert.hello.clickup.ClickUpProps
import com.bkahlert.hello.clickup.clickUpMenu
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.props.prop
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.headless.components.Menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.serialization.json.Json
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

fun PropsStore.clickup(): ClickUp = ClickUp(prop("clickup", { ClickUpProps(null) }, Json))

value class ClickUp(val store: SyncStore<ClickUpProps>) : NavItem, SyncStore<ClickUpProps> by store {

    override val content: (RenderContext.(selection: Store<NavItem?>, placement: Placement) -> Unit)
        get() = { _, _ ->
            div("flex-1 min-w-0 max-w-2xl") {
                data.render { props -> clickUpMenu(props) }
            }
        }
    override val collapsedContent: Menu<HTMLElement>.MenuItems<HTMLDivElement>.(RenderContext, selection: Store<NavItem?>) -> Unit
        get() =
            { renderContext, _ ->
//                            renderContext.div("w-full") {
//                                props.render(into = this) { props ->
//                                    clickUpMenu(
//                                        props?.content?.clickUpProps,
//                                        """
//                                            .ui.menu { border-radius: 0; height: 3rem; background: transparent; }
//                                            * { color: white }
//                                        """.trimIndent()
//                                    )
//                                }
//                            }
            }

    override fun toString(): String {
        return "ClickUp(links=$current)"
    }
}
