package com.bkahlert.hello.props.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.props.domain.Props
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Green
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Red
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.element.Content
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.IconButton
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.element.disabled
import com.bkahlert.semanticui.element.divided
import com.bkahlert.semanticui.element.negative
import com.bkahlert.semanticui.element.positive
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.inverted
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea
import com.bkahlert.semanticui.element.List as SList

@Composable
public fun PropsView(
    propsResource: Resource<Props?>?,
    onUpdate: ((String, JsonObject) -> Unit)? = null,
    loadingState: LoadingState = if (propsResource == null) LoadingState.On else LoadingState.Off,
) {
    when (propsResource) {
        null -> SList({
            apply(loadingState)
            v.divided()
        }) {
            apply(
                loadingState,
                dimmerAttrs = { v.inverted() },
                loaderAttrs = { v.size(Mini) },
                loaderText = "Loading props",
            )
        }

        is Resource.Success -> PropsView(
            props = propsResource.data,
            onUpdate = onUpdate,
            loadingState = loadingState,
        )

        is Resource.Failure -> ErrorMessage(propsResource.message, propsResource.cause)
    }
}

@Composable
public fun PropsView(
    props: Props? = null,
    onUpdate: ((String, JsonObject) -> Unit)? = null,
    loadingState: LoadingState = LoadingState.Off,
) {
    SList({
        apply(loadingState)
        v.divided()
    }) {
        apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Mini) }, loaderText = "Loading props")
        when (props?.content?.size) {
            null -> Item { Header { Em { Text("Unavailable") } } }
            0 -> Item { Header { Em { Text("Empty") } } }
            else -> {
                props.content.forEach { (key, value) ->
                    val initialValue = LenientAndPrettyJson.encodeToString(value)
                    Item {
                        S("ui", "horizontal", "label") { Text(key) }
                        if (onUpdate != null) {
                            var inputValue by remember { mutableStateOf(initialValue) }
                            val isValid by derivedStateOf {
                                kotlin.runCatching {
                                    LenientAndPrettyJson.decodeFromString(
                                        JsonObject.serializer(),
                                        inputValue
                                    )
                                }.isSuccess
                            }
                            val isDirty by derivedStateOf { inputValue != initialValue }
                            Content {
                                S("ui", "form") {
                                    S("field", "error".takeUnless { isValid }) {
                                        Label("Change value")
                                        TextArea(value = inputValue) {
                                            value(inputValue)
                                            onInput { inputValue = it.value }
                                        }
                                    }
                                    S("inline", "fields") {
                                        IconButton({
                                            v.size(Mini).colored(Green).positive()
                                            if (!isDirty || !isValid) s.disabled()
                                            else onClick {
                                                onUpdate(key, LenientAndPrettyJson.decodeFromString(JsonObject.serializer(), inputValue))
                                            }
                                        }) { Icon("check") }
                                        IconButton({
                                            v.size(Mini).colored(Red).negative()
                                            if (isDirty) onClick {
                                                inputValue = initialValue
                                            } else {
                                                s.disabled()
                                            }
                                        }) { Icon("x") }
                                    }
                                }
                            }
                        } else {
                            Pre { Code { Text(initialValue) } }
                        }

                    }
                }
                apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Small) })
            }
        }
    }
}
