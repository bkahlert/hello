package playground.clickupapp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.app.ui.LandingScreen
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.demo.InMemoryPropsDataSource
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoProvider
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

val ClickUpAppDemoProvider: DemoProvider = DemoProvider(
    id = "clickup-app",
    name = "ClickUp App",
    {
        val attemptConnect = false
        Demo("Using Mock (attemptConnect=$attemptConnect)") { demoScope ->
            if (attemptConnect) {
                ClickUpApp(
                    rememberAppViewModel(
                        sessionDataSource = FakeSessionDataSource(initiallyAuthorized = true),
                        propsRepository = PropsRepository(
                            propsDataSource = InMemoryPropsDataSource(Props(buildJsonObject {
                                put("clickup", buildJsonObject { put("api-token", JsonPrimitive("pk_123_abc")) })
                            })),
                            externalScope = demoScope,
                        )
                    )
                )
            } else {
                ClickUpApp()
            }
        }
    },
    {
        Demo("Using Environment") { demoScope ->
            var showLandingScreen by remember { mutableStateOf(true) }
            val environmentRepository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), demoScope) }
            val environmentResource by environmentRepository.environmentFlow().collectAsState(null)
            if (showLandingScreen) {
                LandingScreen(onTimeout = { showLandingScreen = false })
            } else {
                when (val resource = environmentResource) {
                    null -> showLandingScreen = true
                    is Resource.Success -> {
                        ClickUpApp(rememberAppViewModel(environment = resource.data))
                        EnvironmentView(resource.data)
                    }

                    is Resource.Failure -> ErrorMessage(resource.message, resource.cause)
                }
            }
        }
    },
)
