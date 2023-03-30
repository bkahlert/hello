package playground.components

import com.bkahlert.hello.fritz2.inheritStylesheets
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.webcomponents.WebComponent
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.ShadowRoot

object WeatherCard : WebComponent<HTMLDivElement>() {

    private val city: Flow<String> = attributeChanges("city")

    override fun RenderContext.init(element: HTMLElement, shadowRoot: ShadowRoot): HtmlTag<HTMLDivElement> {
        shadowRoot.inheritStylesheets(element)

        return div("p-4 rounded-xl bg-gradient-box-light shadow-sky-light dark:bg-box-darkxxx dark:shadow-orange-dark") {
            h2 { city.renderText() }
            h3 {
                +"Cloudy"
                span {
                    +"Wind 10km/h "
                    span("dot") { +"•" }
                    +" Precip 0%"
                }
            }
            h1 { +"23°" }
            div("sky") {
                div("p-4 rounded-xl bg-gradient-box-light shadow-sky-light dark:bg-box-darkxxx dark:shadow-orange-dark") { }
                div("cloud") {
                    div("circle-small") {}
                    div("circle-tall") {}
                    div("circle-medium") {}
                }
            }
            table {
                tr {
                    td { +"TUE" }
                    td { +"WED" }
                    td { +"THU" }
                    td { +"FRI" }
                    td { +"SAT" }
                }
                tr {
                    td { +"30°" }
                    td { +"34°" }
                    td { +"36°" }
                    td { +"34°" }
                    td { +"37°" }
                }
                tr {
                    td { +"17°" }
                    td { +"22°" }
                    td { +"19°" }
                    td { +"23°" }
                    td { +"19°" }
                }
            }
        }
    }
}
