package playground.components.fritz2

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.icon.headlessui.HeadlessUiIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.icon.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.type
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLDivElement
import playground.components.fritz2.components.checkboxGroupDemo
import playground.components.fritz2.components.collectionDemo
import playground.components.fritz2.components.disclosureDemo
import playground.components.fritz2.components.inputFieldDemo
import playground.components.fritz2.components.listboxDemo
import playground.components.fritz2.components.menuDemo
import playground.components.fritz2.components.modalDemo
import playground.components.fritz2.components.popOverDemo
import playground.components.fritz2.components.radioGroupDemo
import playground.components.fritz2.components.switchDemo
import playground.components.fritz2.components.tabsDemo
import playground.components.fritz2.components.textAreaDemo
import playground.components.fritz2.components.toastDemo
import playground.components.fritz2.components.tooltipDemo
import playground.components.fritz2.foundation.testTrapFocus

sealed interface Page {
    val content: ContentBuilder<HTMLDivElement>
}

data class DemoPage(val title: String, val description: String, val icon: Uri, override val content: ContentBuilder<HTMLDivElement>) :
    Page

data class TestDrive(override val content: ContentBuilder<HTMLDivElement>) : Page

val pages: Map<String, Page> = mapOf(
    "checkboxGroup" to DemoPage(
        "Headless Checkboxgroup",
        """Checkbox groups give you the same functionality as native HTML checkbox inputs, without any of the styling.
            |They're perfect for building out custom UIs for multi selection.""".trimMargin(),
        HeadlessUiIcons.switch,
        RenderContext::checkboxGroupDemo
    ),
    "dataCollection" to DemoPage(
        "Headless DataCollection",
        """A collection handles sorting, filtering of and selecting item form a collection.""".trimMargin(),
        SolidHeroIcons.table_cells,
        RenderContext::collectionDemo
    ),
    "disclosure" to DemoPage(
        "Headless Disclosure",
        """A simple, accessible foundation for building custom UIs that show and hide content, like togglable
            | accordion panels.""".trimMargin(),
        HeadlessUiIcons.disclosure,
        RenderContext::disclosureDemo
    ),
    "inputfield" to DemoPage(
        "Headless Input",
        "Easily create accessible, fully customizable single line text inputs.",
        SolidHeroIcons.pencil,
        RenderContext::inputFieldDemo
    ),
    "listbox" to DemoPage(
        "Headless Listbox",
        """Listboxes are a great foundation for building custom, accessible select menus for your app,
            |complete with robust support for keyboard navigation.""".trimMargin(),
        HeadlessUiIcons.listbox,
        RenderContext::listboxDemo
    ),
    "menu" to DemoPage(
        "Headless Menu",
        """Menus offer an easy way to build custom, accessible dropdown components with robust support for keyboard
            | navigation.""".trimMargin(),
        HeadlessUiIcons.menu,
        RenderContext::menuDemo
    ),
    "modal" to DemoPage(
        "Headless Modal",
        """Menus offer an easy way to build custom, accessible dropdown components with robust support for keyboard
            | navigation.""".trimMargin(),
        HeadlessUiIcons.dialog,
        RenderContext::modalDemo
    ),
    "popover" to DemoPage(
        "Headless Popover",
        """Popovers are perfect for floating panels with arbitrary content like navigation menus, mobile menus and
            | flyout menus.""".trimMargin(),
        HeadlessUiIcons.popover,
        RenderContext::popOverDemo
    ),
    "radioGroup" to DemoPage(
        "Headless Radiogroup",
        """Radio Groups give you the same functionality as native HTML radio inputs, without any of the styling.
            |They're perfect for building out custom UIs for single selection.""".trimMargin(),
        HeadlessUiIcons.radio_group,
        RenderContext::radioGroupDemo
    ),
    "switch" to DemoPage(
        "Headless Switch",
        """Switches are a pleasant interface for toggling a value between two states, and offer the same
            |semantics and keyboard navigation as native checkbox elements.""".trimMargin(),
        HeadlessUiIcons.switch,
        RenderContext::switchDemo
    ),
    "tabGroup" to DemoPage(
        "Headless Tabs",
        """Easily create accessible, fully customizable tab interfaces, with robust focus management and keyboard
            | navigation support.""".trimMargin(),
        HeadlessUiIcons.tabs,
        RenderContext::tabsDemo
    ),
    "textarea" to DemoPage(
        "Headless Textarea",
        "Easily create accessible, fully customizable multi-line text inputs.",
        SolidHeroIcons.pencil,
        RenderContext::textAreaDemo
    ),
    "tooltip" to DemoPage(
        "Headless Tooltip",
        """Some information that is displayed, whenever you hover a target element using your pointer device.""".trimMargin(),
        SolidHeroIcons.chat_bubble_left,
        RenderContext::tooltipDemo
    ),
    "toast" to DemoPage(
        "Headless Toast",
        """Display notification-like content in arbitrary positions on the screen.""".trimMargin(),
        SolidHeroIcons.chat_bubble_oval_left_ellipsis,
        RenderContext::toastDemo
    ),
    "focus" to TestDrive(RenderContext::testTrapFocus)
)

fun RenderContext.overview(store: Store<String>) {
    div("flex flex-col justify-start items-center h-screen") {
        h1("mb-8 tracking-tight font-bold text-gray-900 text-4xl") {
            span("block sm:inline") { +"fritz2" }
            span("block text-primary-800 sm:inline") { +"Headless Demos" }
        }
        div("w-3/4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-12") {
            pages.filter { it.value is DemoPage }.map { (k, v) -> k to v as DemoPage }.forEach { (key, value) ->
                button(
                    """-m-3 p-3 pr-5 flex items-start rounded-lg hover:bg-gray-50 hover:ring-2 hover:ring-white
                    | ring-offset-2 ring-offset-primary-600 hover:outline-none shadow-lg rounded-lg bg-white
                    | opacity-80 hover:opacity-100 transition ease-in-out duration-150""".trimMargin()
                ) {
                    type("button")
                    icon("flex-shrink-0 h-6 w-6", value.icon)
                    div("ml-4 text-left") {
                        p("text-base font-medium text-gray-900") { +value.title }
                        p("mt-1 sm:text-sm text-gray-500") { +value.description }
                    }
                    clicks.map { key } handledBy store.update
                }
            }
        }
    }
}
