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
import com.bkahlert.hello.data.DataRetrieval
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
            val environmentRetrieval by environmentRepository.environmentFlow().collectAsState(DataRetrieval.Ongoing)
            if (showLandingScreen) {
                LandingScreen(onTimeout = { showLandingScreen = false })
            } else {
                when (val retrieval = environmentRetrieval) {
                    is DataRetrieval.Ongoing -> showLandingScreen = true
                    is DataRetrieval.Succeeded -> App(rememberAppViewModel(environment = retrieval.data))
                    is DataRetrieval.Failed -> ErrorMessage(retrieval.cause, retrieval.message)
                }
            }
        }
    },
)
