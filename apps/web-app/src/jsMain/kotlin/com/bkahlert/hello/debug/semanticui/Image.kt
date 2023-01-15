package com.bkahlert.hello.debug.semanticui

import androidx.compose.runtime.Composable
import com.bkahlert.hello.dom.Image
import com.bkahlert.hello.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.hello.semanticui.core.dom.SemanticElement
import com.bkahlert.hello.semanticui.element.ImageImageElement
import org.jetbrains.compose.web.dom.Img

/** Creates a [SemanticUI image](https://semantic-ui.com/elements/image.html). */
@Composable
public fun Image(
    image: Image,
    alt: String = "",
    attrs: SemanticAttrBuilderContext<ImageImageElement>? = null,
): Unit = SemanticElement({ classes("ui"); attrs?.invoke(this); classes("image"); }) { a, _ -> Img(image.dataURI, alt, a) }
