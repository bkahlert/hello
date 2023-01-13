package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.attributes.State
import com.bkahlert.hello.semanticui.attributes.Variation
import com.bkahlert.hello.semanticui.attributes.Variation.Size
import com.bkahlert.hello.semanticui.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.dom.SemanticElement
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
    attrs: SemanticAttrBuilderContext<InputElement, HTMLDivElement>? = null,
    content: SemanticContentBuilder<InputElement, HTMLDivElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("input")
    }, content)
}
