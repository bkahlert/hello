package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.Composable
import com.bkahlert.kommons.net.Uri
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.TextAlignment
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.element.Content
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.LinkList
import com.bkahlert.semanticui.element.SubHeader
import com.bkahlert.semanticui.element.aligned
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.padded
import com.bkahlert.semanticui.element.size
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text
import com.bkahlert.semanticui.element.List as SemanticList

@Composable
public fun DemoDemos() {
    Header { Text("Demo") }
    DemoVariants()

    Header { Text("Demos") }
    Demos("Empty")
    Demos("With Attribute (Right Aligned)", { v.aligned(TextAlignment.Right) })
    Demos("With Content") { DemoVariants() }
    Demos("With Attribute (Right Aligned) and Content", { v.aligned(TextAlignment.Right) }) { DemoVariants() }
    Demos("Nested") {
        Demos("Nested A") { DemoVariants() }
        Demos("Nested B") { DemoVariants() }
    }

    Header { Text("SemanticDemo") }
    SemanticDemo(
        null,
        "Name",
        SemanticDemoSection.Types {
            Demo("Type A") { LoremIpsumParagraph() }
            Demo("Type B") { LoremIpsumParagraph() }
        },
        SemanticDemoSection.Variations {
            Demo("Variation 1") { LoremIpsumParagraph() }
            Demos("Variation 2") {
                Demo("Variation 2.1") { LoremIpsumParagraph() }
                Demo("Variation 2.2") { LoremIpsumParagraph() }
            }
        },
    ).invoke()
}

@Suppress("NOTHING_TO_INLINE")
@Composable
private inline fun DemoVariants() {
    Demo("No Content")
    Demo("With Content") { LoremIpsumParagraph() }
    Demo("With Content And Warning", warning = "This is just a test") { LoremIpsumParagraph() }
    Demo("With Content (Basic)", basic = true) { LoremIpsumParagraph() }
    Demo("With Attribute (Padded) and Content", { v.padded() }) { LoremIpsumParagraph() }
}

public enum class SemanticType(
    private val slug: String,
) {
    Element("elements"), Collection("collection"), View("views"), Module("modules");

    public fun linkOf(widget: String): Uri =
        Uri.parse("https://semantic-ui.com/$slug/${widget.lowercase()}.html")
}

public class SemanticDemo(
    public val type: SemanticType?,
    public val element: String,
    public vararg val sections: SemanticDemoSection,
) {
    @Composable public operator fun invoke() {
        if (type != null) {
            LinkList({ v.horizontal() }) {
                A(type.linkOf(element).toString(), { classes("item") }) {
                    Image("https://semantic-ui.com/images/logo.png") { v.size(Mini) }
                    S("content") {
                        SubHeader { Text(type.name) }
                        Text(element)
                    }
                }
            }
        } else {
            SemanticList({ v.horizontal() }) {
                Item {
                    Image(HELLO_FAVICON) { v.size(Mini) }
                    Content {
                        SubHeader { Text("Custom") }
                        Text(element)
                    }
                }
            }
        }
        sections.forEach { section ->
            SubHeader { Text(section.name) }
            section.content()
        }
    }
}

public sealed class SemanticDemoSection(
    public val name: String,
    public val content: @Composable () -> Unit,
) {
    public class Types(content: @Composable () -> Unit) : SemanticDemoSection("Types", content)
    public class Groups(content: @Composable () -> Unit) : SemanticDemoSection("Groups", content)
    public class Content(content: @Composable () -> Unit) : SemanticDemoSection("Content", content)
    public class States(content: @Composable () -> Unit) : SemanticDemoSection("States", content)
    public class Variations(content: @Composable () -> Unit) : SemanticDemoSection("Variations", content)
    public class GroupVariations(content: @Composable () -> Unit) : SemanticDemoSection("GroupVariations", content)
}

@Suppress("LongLine")
private const val HELLO_FAVICON =
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIxMSA5IDEwNyAxMTAiIGN1cnNvcj0iZGVmYXVsdCI+PHN0eWxlPjwhW0NEQVRBWy5Ce21peC1ibGVuZC1tb2RlOm11bHRpcGx5fV1dPjwvc3R5bGU+PGRlZnM+PHBhdGggaWQ9IkEiIGQ9Ik0xMSAxMTVWMTNhNCA0IDAgMCAxIDQtNGgxOGE0IDQgMCAwIDEgNCA0djM0YTQgNCAwIDAgMCA0IDRoMzRhNCA0IDAgMCAwIDQtNFYxM2E0IDQgMCAwIDEgNC00aDE4YTQgNCAwIDAgMSA0IDR2MTAyYTQgNCAwIDAgMS00IDRIODNhNCA0IDAgMCAxLTQtNFY3NmE0IDQgMCAwIDAtNC00SDQxYy0yLjIgMC00IDEuOC00IDR2MzlhNCA0IDAgMCAxLTQgNEgxNWE0IDQgMCAwIDEtNC00eiIvPjxnIGlkPSJCIj48cmVjdCB4PSI5MiIgeT0iOTMiIHdpZHRoPSIyNiIgaGVpZ2h0PSIyNiIgcng9IjQiLz48cGF0aCBkPSJNOTEuNSAxM2MtLjItMi4zNSAyLjE2LTQgNC41Mi00SDk2aDE3LjQ5YzIuMzQgMCA0LjE4IDEuOTkgMy45OSA0LjMybC00LjY2IDY0LjA2YTMuOTkgMy45OSAwIDAgMS0zLjk3IDMuNjJoLTguNTJjLTIuMDUgMC0zLjc2LTEuNTUtMy45Ni0zLjU5TDkxLjUgMTMiLz48L2c+PC9kZWZzPjx1c2UgeGxpbms6aHJlZj0iI0EiIGNsYXNzPSJCIiBmaWxsPSIjYzIxZjczIi8+PGcgZmlsbD0iIzI5YWFlMiI+PHVzZSB4bGluazpocmVmPSIjQiIgY2xhc3M9IkIiLz48dXNlIHhsaW5rOmhyZWY9IiNCIj48YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJvcGFjaXR5IiBjYWxjTW9kZT0ic3BsaW5lIiBkdXI9IjE1cyIgdmFsdWVzPSIwOzE7MCIga2V5VGltZXM9IjA7MC41OzEiIGtleVNwbGluZXM9IjAuNDIgMCAwLjU4IDE7MC40MiAwIDAuNTggMSIgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiLz48L3VzZT48L2c+PC9zdmc+"
