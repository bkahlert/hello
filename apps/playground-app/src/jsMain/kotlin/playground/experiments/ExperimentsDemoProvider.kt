package playground.experiments

import com.bkahlert.semanticui.demo.Column
import com.bkahlert.semanticui.demo.DemoProvider
import com.bkahlert.semanticui.demo.Grid
import playground.experiments.animation.ColorAnimationDemos

val ExperimentsDemoProvider: DemoProvider = DemoProvider("experiments", "Experiments") {
    Grid {
        Column {
            ColorAnimationDemos()
            FixturesDemos()
        }
        Column {
            MutableFlowStateDemo()
            ViewModelDemo()
        }
    }
}
