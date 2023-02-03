package com.bkahlert.hello.props.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.props.data.PropsRepository
import com.bkahlert.hello.props.domain.GetPropsRepositoryUseCase
import com.bkahlert.hello.props.domain.GetPropsUseCase
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.hello.props.domain.RefreshPropsUseCase
import com.bkahlert.hello.props.domain.SetPropUseCase
import com.bkahlert.hello.props.ui.PropsView
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.LoadingState.Indeterminate
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.size
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
        Demo("Dynamically filled") {
            val repository = remember {
                PropsRepository(
                    InMemoryPropsDataSource(
                        Props(
                            buildJsonObject {
                                put("foo", buildJsonObject { put("bar", JsonPrimitive(42)) })
                                put("baz", JsonNull)
                            }
                        ),
                    )
                )
            }
            val getPropsRepository = remember { GetPropsRepositoryUseCase(repository) }
            val getProps = remember { GetPropsUseCase(getPropsRepository) }
            val refreshProps = remember { RefreshPropsUseCase(getPropsRepository) }
            val setProp = remember { SetPropUseCase(getPropsRepository) }

            val props: Props? by getProps().collectAsState(null)
            var refreshing by remember(props) { mutableStateOf(false) }

            val scope = rememberReportingCoroutineScope()
            LabeledIconButton({
                if (refreshing) s.disabled()
                v.size(Mini)
                onClick {
                    scope.launch {
                        refreshing = true
                        refreshProps()
                    }
                }
            }) {
                Icon("sync", loading = refreshing)
                Text("Refresh")
            }

            when (val current = props) {
                null -> PropsView(loadingState = Indeterminate)
                else -> {
                    if (!refreshing) {
                        Header { Text("Saved") }
                        PropsView(
                            props = current,
                            loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
                        )
                    }

                    Header { Text("Edit") }
                    PropsView(
                        props = current,
                        onUpdate = { id, value -> scope.launch { setProp(id, value) } },
                        loadingState = if (refreshing) LoadingState.On else LoadingState.Off,
                    )
                }
            }
        }
    }
}
