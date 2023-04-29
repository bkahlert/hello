package playground.fritz2.headlessdemo

import com.bkahlert.hello.fritz2.ContentBuilder
import dev.fritz2.core.RenderContext
import org.w3c.dom.HTMLDivElement

fun RenderContext.example(title: String, content: ContentBuilder<HTMLDivElement>) =
    section("mt-16") {
        div("border rounded-md shadow-sm border-primary-500 ring-2 ring-primary-500 ") {
            p("p-2 rounded-t-md font-medium sm:text-sm bg-primary-100") { +title }
            div("p-8") {
                content()
            }
        }
    }

fun RenderContext.result(content: ContentBuilder<HTMLDivElement>) = div(
    """mt-4 p-2.5
    | bg-primary-100 rounded shadow-sm
    | ring-2 ring-primary-500
    | text-sm text-primary-800
    | focus:outline-none focus:ring-4 focus:ring-primary-600 focus:border-primary-800""".trimMargin(),
    id = "result"
) {
    attr("tabindex", "0")
    content()
}
