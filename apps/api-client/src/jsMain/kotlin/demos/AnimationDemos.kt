package demos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Colors
import com.bkahlert.semanticui.custom.Demo
import com.bkahlert.semanticui.custom.Demos
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.dom.Div
import playground.ColoredTile
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun AnimationDemos() {
    Demos("Animation") {
        Demo("Color animation") {
            val colorScope = rememberCoroutineScope()
            val baseColor = Colors.blue.toHSL()
            val colorsCount = 25
            var hue by mutableStateOf(0.0)
            val colors: MutableList<Color> = mutableStateListOf()
            colorScope.launch {
                while (true) {
                    while (colors.size >= colorsCount) {
                        colors.removeFirst()
                    }
                    while (colors.size < colorsCount) {
                        colors.add(Color.HSL(hue, baseColor.saturation, baseColor.lightness, .75))
                        hue = (hue + 0.005).mod(1.0)
                    }
                    delay(40.milliseconds)
                }
            }
            Div {
                colors.forEachIndexed { index, color ->
                    ColoredTile(color, "") { style { height((index * 0.1).em) } }
                }
            }
        }
    }
}
