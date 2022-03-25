package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
inline fun <T> Result<T>?.visualize(
    showSpinner: Boolean = true,
    visualizeFailure: @Composable (Throwable) -> Unit = @Composable { ErrorMessage(it) },
    visualizeSuccess: (T) -> Unit,
) {
    this?.fold(
        { visualizeSuccess(it) },
        { visualizeFailure(it) }
    ) ?: run { if (showSpinner) Div { Small { Text("Loading...") } } }
}

@Composable
fun <T : List<E>, E> Result<T>?.visualizeEach(
    showSpinner: Boolean = true,
    visualizeFailure: @Composable (Throwable) -> Unit = @Composable { ErrorMessage(it) },
    visualizeSuccess: (E) -> Unit,
) {
    this?.fold(
        { it.forEach(visualizeSuccess) },
        { visualizeFailure(it) }
    ) ?: run {
        if (showSpinner) Div { Small { Text("Loading...") } }
    }
}
