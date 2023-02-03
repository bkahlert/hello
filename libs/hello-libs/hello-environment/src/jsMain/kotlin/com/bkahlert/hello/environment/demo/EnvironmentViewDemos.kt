package com.bkahlert.hello.environment.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.data.EnvironmentRepository
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.environment.domain.GetEnvironmentUseCase
import com.bkahlert.hello.environment.domain.RefreshEnvironmentUseCase
import com.bkahlert.hello.environment.ui.EnvironmentView
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Text

@Composable
public fun EnvironmentViewDemos() {
    Demos("EnvironmentView") {
        Demo("Empty") {
            EnvironmentView(Environment.EMPTY)
        }
        Demo("Statically filled") {
            EnvironmentView(Environment("FOO" to "bar", "BAZ" to ""))
        }
        Demo("Dynamically filled") {
            val repository = remember { EnvironmentRepository(DynamicEnvironmentDataSource()) }
            val getEnvironment = remember { GetEnvironmentUseCase(repository) }
            val refreshEnvironment = remember { RefreshEnvironmentUseCase(repository) }

            val environment by getEnvironment().collectAsState(Environment.EMPTY)
            var refreshing by remember(environment) { mutableStateOf(false) }

            val scope = rememberReportingCoroutineScope()
            LabeledIconButton({
                if (refreshing) s.disabled()
                v.size(Mini)
                onClick {
                    scope.launch {
                        refreshing = true
                        refreshEnvironment()
                    }
                }
            }) {
                Icon("sync", loading = refreshing)
                Text("Refresh")
            }

            EnvironmentView(environment)
        }
    }
}
