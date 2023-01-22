package playground.app.demo

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid
import org.jetbrains.compose.web.dom.Text

val AppDemoProvider: DemoProvider = DemoProvider("app", "App") {
    Grid {
        Column {
            AppDemos()
        }
        Column {
            Text("—")
        }
    }
}
