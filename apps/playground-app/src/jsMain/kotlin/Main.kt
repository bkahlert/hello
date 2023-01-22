import com.bkahlert.hello.clickup.demo.ClickUpDemos
import com.bkahlert.hello.clickup.viewmodel.ClickUpStyleSheet
import com.bkahlert.kommons.binding.adapt
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.demo.DemoView
import com.bkahlert.semanticui.demo.SemanticUiDemoProviders
import com.bkahlert.semanticui.devmode.BoundDemoViewState
import com.bkahlert.semanticui.devmode.bindFragmentParameter
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLDivElement
import playground.app.DispatchingLoaderDemos
import playground.app.demo.AppDemoProvider
import playground.experiments.ExperimentsDemoProvider
import playground.experiments.FixturesDemos

fun main() {

    document.getElementById("root")?.unsafeCast<HTMLDivElement>()?.style?.padding = "1em"

    val activeDemoBinding = window.bindFragmentParameter("demo").adapt(
        from = { it?.firstOrNull() },
        to = { listOfNotNull(it) },
    )

    renderComposable(rootElementId = "root") {
        Style(ClickUpStyleSheet)
        DemoView(
            AppDemoProvider,
            ExperimentsDemoProvider,
            ClickUpDemos,
            *SemanticUiDemoProviders,
            state = BoundDemoViewState(activeDemoBinding),
        ) {
            S("ui", "vertical", "stripe", "segment") {
                S("ui equal width stackable grid") {
                    S("row") {
                        S("column") {
                            FixturesDemos()
                        }
                        S("column") {
                            DispatchingLoaderDemos()
                        }
                    }
                }
            }
        }
    }
}
