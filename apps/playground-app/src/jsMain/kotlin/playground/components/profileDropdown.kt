package playground.components

import com.bkahlert.kommons.js.ConsoleLogger
import dev.fritz2.core.Handler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.transition
import dev.fritz2.headless.components.menu
import dev.fritz2.headless.foundation.utils.popper.Placement
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLButtonElement
import com.bkahlert.hello.fritz2.icon

fun RenderContext.profileDropdown(
    classes: String? = null,
    items: List<NavItemGroup>,
    button: Tag<HTMLButtonElement>.(String) -> Unit,
) {

    val action = object : RootStore<NavItem?>(null) {
        val logger = ConsoleLogger("ProfileDropdown")
        val log: Handler<NavItem> = handle { _, item ->
            item.also { logger.debug("selected, item") }
        }
    }

    menu(classes) {
        div {
            menuButton(
                classes(
                    "flex",
                    "rounded-full bg-gray-800",
                    "focus:outline-none focus-visible:ring focus-visible:ring-white focus-visible:ring-opacity-75"
                )
            ) {
                className(opened.map { if (it) "ring ring-white ring-opacity-75" else "" })
                button(this, "shrink-0 h-8 w-8 rounded-full")
                span("sr-only") { opened.map { if (it) "Close Menu" else "Open Menu" }.renderText() }
            }
        }

        menuItems(
            classes(
                "absolute right-0 origin-top-right",
                "w-48",
                "rounded-md",
                "box-shadow box-glass",
                "focus:outline-none"
            )
        ) {
            placement = Placement.bottomEnd
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

            items.forEach { group ->
                div("px-1 py-1") {
                    group.forEach { item ->
                        menuItem(
                            classes(
                                "group",
                                "flex w-full items-center",
                                "rounded-md",
                                "px-2 py-2",
                                "text-left",
                                "font-medium sm:text-sm",
                            )
                        ) {
                            className(active.combine(disabled) { a, d ->
                                if (a && !d) "box-glass"
                                else if (d) "opacity-50 cursor-default" else ""
                            })
                            active.render { a ->
                                icon("shrink-0 mr-2 h-4 w-4", if (a) item.activeIcon else item.icon)
                            }
                            +item.label
                            if (item.disabled) disable(true)
                            selected.map { item } handledBy action.log
                        }
                    }
                }
            }
        }
    }
}
