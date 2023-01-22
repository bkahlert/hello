package playground.experiments

import androidx.compose.runtime.Composable
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.kommons.color.Colors
import com.bkahlert.semanticui.custom.cssColorValue
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Image
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.border
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px

@Composable
fun FixturesDemos() {
    Demos("Fixtures") {
        Demo("Images") {
            ImageFixtures.forEach { uri ->
                Image(uri) {
                    style {
                        maxWidth(3.em)
                        maxHeight(3.em)
                        margin(.5.em)
                        padding(.5.em)
                        border(1.px, LineStyle.Dotted, Colors.border.cssColorValue)
                        display(DisplayStyle.InlineBlock)
                    }
                }
            }
        }
    }
}
