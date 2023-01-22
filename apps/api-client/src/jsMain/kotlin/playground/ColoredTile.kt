package playground

import androidx.compose.runtime.Composable
import com.bkahlert.kommons.color.Color
import com.bkahlert.semanticui.custom.backgroundColor
import com.bkahlert.semanticui.custom.color
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun ColoredTile(
    color: Color,
    name: String = color.toString(),
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Div({
        style {
            color(color.textColor)
            width(50.px)
            height(50.px)
            backgroundColor(color)
            fontSize(.7.em)
        }
        attrs?.invoke(this)
    }) { Small { Text(name) } }
}
