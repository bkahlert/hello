package playground.experiments.animation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.HSL
import com.bkahlert.kommons.color.Colors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.pc
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ColorAnimation(
    colorCount: Int = 25,
    baseColor: HSL = Colors.blue.toHSL(),
    alpha: Double = .75,
) {
    val colorScope = rememberCoroutineScope()
    var hue by remember { mutableStateOf(0.0) }
    val colors: MutableList<Color> = mutableStateListOf()
    colorScope.launch {
        while (true) {
            while (colors.size >= colorCount) {
                colors.removeFirst()
            }
            while (colors.size < colorCount) {
                colors.add(HSL(hue, baseColor.saturation, baseColor.lightness, alpha))
                hue = (hue + 0.005).mod(1.0)
            }
            delay(40.milliseconds)
        }
    }
    Div {
        colors.forEachIndexed { index, color ->
            ColoredTile(color, "") {
                style {
                    width(25.pc)
                    height((index * 0.1).em)
                }
            }
        }
    }
}
