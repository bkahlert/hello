package com.bkahlert.hello.environment.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.bkahlert.hello.data.Resource.Failure
import com.bkahlert.hello.data.Resource.Success
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.environment.domain.GetEnvironmentUseCase
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Loader

@Composable
public fun EnvironmentViewDemos() {
    Demos("EnvironmentView") {
        Demo("Empty") {
            EnvironmentView(Environment.EMPTY)
        }
        Demo("Statically filled") {
            EnvironmentView(Environment("FOO" to "bar", "BAZ" to ""))
        }
        Demo("Dynamically filled") { demoScope ->
            val repository = remember { EnvironmentRepository(DynamicEnvironmentDataSource(), demoScope) }
            val getEnvironment = remember { GetEnvironmentUseCase(repository) }

            val environmentResource by getEnvironment().collectAsState(null)

            when (val resource = environmentResource) {
                null -> Loader("Loading environment")
                is Success -> EnvironmentView(resource.data)
                is Failure -> ErrorMessage(resource.message, resource.cause)
            }
        }
    }
}
