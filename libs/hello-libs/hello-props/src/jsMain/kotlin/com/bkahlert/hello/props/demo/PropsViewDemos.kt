package com.bkahlert.hello.props.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.domain.GetPropsUseCase
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.props.domain.SetPropUseCase
import com.bkahlert.hello.props.ui.PropsView
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Header
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.jetbrains.compose.web.dom.Text

@Composable
public fun PropsViewDemos() {
    Demos("PropsView") {
        Demo("Empty") {
            PropsView()
        }
        Demo("Filled") {
            PropsView(
                Props(
                    buildJsonObject {
                        put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                        put("baz", JsonNull)
                    }
                ),
            )
        }
        Demo("Dynamically filled") { demoScope ->
            val repository = remember {
                PropsRepository(
                    InMemoryPropsDataSource(
                        Props(
                            buildJsonObject {
                                put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                                put("baz", JsonNull)
                            }
                        ),
                    ),
                    demoScope,
                )
            }
            val getProps = remember { GetPropsUseCase(repository) }
            val setProp = remember { SetPropUseCase(repository) }

            val propsResource: Resource<Props?>? by getProps().collectAsState(null)
            var refreshing by remember(propsResource) { mutableStateOf(propsResource == null) }

            if (!refreshing) {
                Header { Text("Saved") }
                PropsView(
                    propsResource = propsResource,
                    loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
                )
            }

            Header { Text("Edit") }
            PropsView(
                propsResource = propsResource,
                onUpdate = { id, value ->
                    demoScope.launch {
                        refreshing = true
                        setProp(id, value)
                    }
                },
                loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
            )
        }
    }
}
