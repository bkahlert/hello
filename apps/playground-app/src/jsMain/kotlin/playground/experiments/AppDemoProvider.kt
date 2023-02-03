package playground.experiments

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoProvider

val AppDemoProvider: DemoProvider = DemoProvider(
    id = "experiments",
    name = "Experiments",
    {
        Demo("Using Mock") {
            App()
        }
    },
    {
        Demo("Using Environment") {
            var showLandingScreen by remember { mutableStateOf(true) }
            val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource()) }
            val environment by environmentRepository.getEnvironmentFlow().collectAsState(Environment.EMPTY)
            LaunchedEffect(environmentRepository) {
                environmentRepository.refreshEnvironment()
            }
            if (showLandingScreen) {
                LandingScreen(onTimeout = { showLandingScreen = false })
            } else {
                val appState = rememberAppViewModel(
                    environment = environment,
//                sessionDataSource = FakeSessionDataSource(initiallyAuthorized = true),
                )
                App(appState)
            }
            EnvironmentView(environment)
        }
    },
)
