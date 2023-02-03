package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.collection.IconMessage
import com.bkahlert.semanticui.collection.warning
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.element.Segment
import com.bkahlert.semanticui.element.padded
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

/**
 * Renders a [Segment] containing
 * the specified [options].
 */
@Composable
public fun Options(
    options: List<OptionContentBuilder>,
) {
    when (options.size) {
        0 -> IconMessage("attention", { v.warning() }) {
            P { Text("No options available") }
        }

        1 -> Segment({ v.padded() }) {
            S("ui", "one", "column", "center", "aligned", "grid") {
                S("middle", "aligned", "column") {
                    options[0]()
                }
            }
        }

        2 -> Segment {
            S("ui", "stackable", "two", "column", "center", "aligned", "very", "relaxed", "internally", "celled", "grid") {
                S("middle", "aligned", "row") {
                    S("column") {
                        options[0]()
                    }
                    S("column") {
                        options[1]()
                    }
                }
            }
        }

        else -> Segment({ v.padded() }) {
            options.forEachIndexed { index, option ->
                if (index > 0) S("ui", "horizontal", "divider", attrs = {
                    style {
                        marginTop(2.cssRem)
                        marginBottom(2.cssRem)
                    }
                }) { Text("Or") }
                option()
            }
        }
    }
}

/**
 * Renders a [Segment] containing
 * the specified [options].
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
public inline fun Options(
    vararg options: OptionContentBuilder
): Unit = Options(options.asList())

/**
 * Renders a [Segment] containing
 * the results of applying the given [transform] function to each element of
 * the specified [options].
 */
@Composable
public inline fun <T> Options(
    vararg options: T,
    transform: (T) -> OptionContentBuilder,
): Unit = Options(options.map { transform(it) })

/**
 * Renders a [Segment] containing
 * the results of applying the given [transform] function to each element of
 * the specified [options].
 */
@Composable
public inline fun <T> Options(
    options: List<T>,
    transform: (T) -> OptionContentBuilder,
): Unit = Options(options.map { transform(it) })

public typealias OptionContentBuilder = SemanticContentBuilder<SemanticElement<HTMLDivElement>>
