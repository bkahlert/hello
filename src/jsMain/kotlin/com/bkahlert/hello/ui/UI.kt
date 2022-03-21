package com.bkahlert.hello.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.Failure
import com.bkahlert.hello.Response
import com.bkahlert.hello.Success
import com.bkahlert.kommons.fix.value
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text

@Composable
inline fun <T> Response<T>?.visualize(
    showSpinner: Boolean = true,
    visualizeFailure: @Composable (Throwable) -> Unit = @Composable { ErrorMessage(it) },
    visualizeSuccess: (T) -> Unit,
) {
    when (this) {
        null -> {
            if (showSpinner) Div { Small { Text("Loading...") } }
        }
        is Failure -> visualizeFailure(value)
        is Success -> visualizeSuccess(value)
    }
}

@Composable
fun <T : List<E>, E> Response<T>?.visualizeEach(
    showSpinner: Boolean = true,
    visualizeFailure: @Composable (Throwable) -> Unit = @Composable { ErrorMessage(it) },
    visualizeSuccess: (E) -> Unit,
) {
    when (this) {
        null -> {
            if (showSpinner) Div { Small { Text("Loading...") } }
        }
        is Failure -> visualizeFailure(value)
        is Success -> value.forEach(visualizeSuccess)
    }
}
