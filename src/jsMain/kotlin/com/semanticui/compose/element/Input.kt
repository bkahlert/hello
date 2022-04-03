package com.semanticui.compose.element

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticAttrsScope
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.SemanticDivElement
import com.semanticui.compose.SemanticElement
import com.semanticui.compose.State
import com.semanticui.compose.Variation
import com.semanticui.compose.Variation.Size
import org.w3c.dom.HTMLDivElement

interface InputElement : SemanticElement

/** [Focus](https://semantic-ui.com/elements/input.html#focus) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.focus: State get() = State.Focus

/** [Loading](https://semantic-ui.com/elements/input.html#loading) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.loading: State get() = State.Loading

/** [Disabled](https://semantic-ui.com/elements/input.html#disabled) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.disabled: State get() = State.Disabled

/** [Error](https://semantic-ui.com/elements/input.html#error) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.error: State get() = State.Error

/** [Icon](https://semantic-ui.com/elements/input.html#icon) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.icon: Variation get() = Variation.Icon

/** [Labeled](https://semantic-ui.com/elements/input.html#labeled) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.labeled: Variation get() = Variation.Labeled

/** [Action](https://semantic-ui.com/elements/input.html#action) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.action: Variation get() = Variation.Action

/** [Transparent](https://semantic-ui.com/elements/input.html#transparent) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.transparent: Variation get() = Variation.Transparent

/** [Inverted](https://semantic-ui.com/elements/input.html#inverted) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.inverted: Variation get() = Variation.Inverted

/** [Fluid](https://semantic-ui.com/elements/input.html#fluid) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.fluid: Variation get() = Variation.Fluid

/** [Size](https://semantic-ui.com/elements/input.html#size) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused") val <TSemantic : InputElement> SemanticAttrsScope<TSemantic, *>.size: Size get() = Variation.Size

/** Creates a [SemanticUI input](https://semantic-ui.com/elements/input.html). */
@Composable
fun Input(
    attrs: SemanticAttrBuilder<InputElement, HTMLDivElement>? = null,
    content: SemanticBuilder<InputElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("input")
    }, content)
}
