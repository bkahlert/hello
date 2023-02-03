package playground.architecture

import com.bkahlert.hello.demo.DemoImageFixtures
import com.bkahlert.semanticui.demo.DemoProvider

val ArchitectureDemoProvider: DemoProvider = DemoProvider(
    id = "playground/architecture",
    name = "Architecture",
    logo = DemoImageFixtures.AndroidRobot,
    {
        ViewModelDemo()
    },
)
