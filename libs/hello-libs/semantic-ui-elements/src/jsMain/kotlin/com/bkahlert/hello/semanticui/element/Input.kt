package com.bkahlert.hello.semanticui.element

import androidx.compose.runtime.Composable
import com.bkahlert.hello.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.hello.semanticui.core.attributes.State
import com.bkahlert.hello.semanticui.core.attributes.Variation
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.hello.semanticui.core.dom.SemanticDivElement
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import org.w3c.dom.HTMLDivElement

public interface InputElement : SemanticElement<HTMLDivElement>

/** [Focus](https://semantic-ui.com/elements/input.html#focus) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.focus: State get() = State.Focus

/** [Loading](https://semantic-ui.com/elements/input.html#loading) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.loading: State get() = State.Loading

/** [Disabled](https://semantic-ui.com/elements/input.html#disabled) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.disabled: State get() = State.Disabled

/** [Error](https://semantic-ui.com/elements/input.html#error) state of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.error: State get() = State.Error

/** [Icon](https://semantic-ui.com/elements/input.html#icon) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.icon: Variation get() = Variation.Icon

/** [Labeled](https://semantic-ui.com/elements/input.html#labeled) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.labeled: Variation get() = Variation.Labeled

/** [Action](https://semantic-ui.com/elements/input.html#action) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.action: Variation get() = Variation.Action

/** [Transparent](https://semantic-ui.com/elements/input.html#transparent) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.transparent: Variation get() = Variation.Transparent

/** [Inverted](https://semantic-ui.com/elements/input.html#inverted) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.inverted: Variation get() = Variation.Inverted

/** [Fluid](https://semantic-ui.com/elements/input.html#fluid) variation of a [input](https://semantic-ui.com/elements/input.html). */
@Suppress("unused")
public val <TSemantic : InputElement> SemanticAttrsScope<TSemantic>.fluid: Variation get() = Variation.Fluid

/** [Variation.Size](https://semantic-ui.com/elements/input.html#size) */
public fun SemanticAttrsScope<InputElement>.v(value: Variation.Size): Unit = variation(value)

/** Creates a [SemanticUI input](https://semantic-ui.com/elements/input.html). */
@Composable
public fun Input(
    attrs: SemanticAttrBuilderContext<InputElement>? = null,
    content: SemanticContentBuilder<InputElement>? = null,
) {
    SemanticDivElement({
        classes("ui")
        attrs?.invoke(this)
        classes("input")
    }, content)
}
