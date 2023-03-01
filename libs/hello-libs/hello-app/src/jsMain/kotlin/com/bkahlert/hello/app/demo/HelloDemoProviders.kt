package com.bkahlert.hello.app.demo

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.app.ui.App
import com.bkahlert.hello.app.ui.HelloImageFixtures
import com.bkahlert.hello.app.ui.LandingScreen
import com.bkahlert.hello.app.ui.rememberAppViewModel
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoProvider

public val HelloAppDemoProvider: DemoProvider = DemoProvider(
    id = "hello-apps",
    name = "Apps",
    logo = HelloImageFixtures.HelloFavicon,
    {
        Demo("Using Mock") {
            App()
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
                    is Success -> App(rememberAppViewModel(environment = resource.data))
                    is Failure -> ErrorMessage(resource.message, resource.cause)
                }
            }
        }
    },
)
