package com.bkahlert.hello.environment.ui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.divided
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SList

@Composable
public fun EnvironmentView(
    environment: Environment,
) {
    SList({ v.divided() }) {
        when (environment.size) {
            0 -> {
                Item {
                    Header { Em { Text("Empty") } }
                }
            }

            else -> environment.forEach { (name, value) ->
                Item {
                    Header { Text(name) }
                    Text(value)
                }
            }
        }
    }
}
