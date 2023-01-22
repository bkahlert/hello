import com.bkahlert.semanticui.core.S
import demos.AnimationDemos
import demos.AppDemos
import demos.LoaderDemos
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        S("ui", "vertical", "stripe", "segment") {
            S("ui equal width stackable grid") {
                S("center aligned row") {
                    S("column") {
                        AppDemos()
                    }
                    S("column") {
                        AnimationDemos()
                    }
                    S("column") {
                        LoaderDemos()
                    }
                }
            }
        }
    }
}
