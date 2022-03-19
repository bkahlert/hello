package com.bkahlert.hello.ui.demo

import androidx.compose.runtime.Composable
import com.semanticui.compose.SemanticAttrBuilder
import com.semanticui.compose.SemanticBuilder
import com.semanticui.compose.element.Header
import com.semanticui.compose.element.Segment
import com.semanticui.compose.element.SegmentElement
import com.semanticui.compose.element.SegmentElementType
import com.semanticui.compose.element.Segments
import com.semanticui.compose.element.SubHeader
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun Demo(
    name: String,
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    Segment(attrs) {
        SubHeader { Text(name) }
        content?.invoke(this)
    }
}

@Composable
fun Demos(
    name: String,
    attrs: SemanticAttrBuilder<SegmentElement, HTMLDivElement>? = null,
    content: SemanticBuilder<SegmentElement, HTMLDivElement>? = null,
) {
    Segments(SegmentElementType.Raised, attrs) {
        Header({
            +Attached.Top
            style { property("border-bottom-width", "0") }
        }) { Text(name) }
        content?.invoke(this)
    }
}

@Suppress("SpellCheckingInspection", "JsonStandardCompliance")
const val SPACER = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAA" +
    "ALAAAAAABAAEAAAIBRAA7"

@Suppress("SpellCheckingInspection", "JsonStandardCompliance")
const val JOHN = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAMAA" +
    "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" +
    "SFQiZbgWV9tAAAAAElFTkSuQmCC"
