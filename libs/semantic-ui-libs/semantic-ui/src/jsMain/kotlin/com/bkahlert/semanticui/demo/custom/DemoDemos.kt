package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.SemanticUiLogo
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Basic
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Colored.Red
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Mini
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.TextAlignment
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.DemoSandbox
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.element.Content
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.Item
import com.bkahlert.semanticui.element.LabeledIconButton
import com.bkahlert.semanticui.element.LinkList
import com.bkahlert.semanticui.element.SubHeader
import com.bkahlert.semanticui.element.aligned
import com.bkahlert.semanticui.element.colored
import com.bkahlert.semanticui.element.compact
import com.bkahlert.semanticui.element.horizontal
import com.bkahlert.semanticui.element.padded
import com.bkahlert.semanticui.element.size
import kotlinx.coroutines.launch
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
    DemoSandbox("Sandboxed") {
        val scope = rememberCoroutineScope()
        LabeledIconButton({
            v.run { +Basic }
            v.colored(Red).compact()
            onClick { scope.launch { error("This is just a test") } }
        }) {
            Icon("exclamation", "circle")
            Text("Throw exception")
        }
    }
}

/**
 * @see <a href="https://semantic-ui.com/introduction/glossary.html#types-of-components">Types of Components</a>
 */
public enum class ComponentType(
    private val slug: String,
) {
    Element("elements"), Collection("collection"), View("views"), Module("modules");

    public fun linkOf(widget: String): Uri =
        Uri.parse("https://semantic-ui.com/$slug/${widget.lowercase()}.html")
}

public class SemanticDemo(
    public val type: ComponentType?,
    public val element: String,
    public vararg val sections: SemanticDemoSection,
) {
    @Composable public operator fun invoke() {
        if (type != null) {
            LinkList({ v.horizontal() }) {
                A(type.linkOf(element).toString(), { classes("item") }) {
                    Image(SemanticUiLogo) { v.size(Mini) }
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

private const val HELLO_FAVICON = "" +
    "data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC" +
    "9zdmciIGFyaWEtbGFiZWw9IkhlbGxvISIgcm9sZT0iaW1nIiBjdXJzb3I9ImRlZmF1bHQiIH" +
    "ZpZXdCb3g9IjAgMCAxMjggMTI4Ij4KICAgIDxwYXRoIGZpbGw9IiNjMjFmNzMiCiAgICAgIC" +
    "AgICBkPSJtMTEsMTE1VjEzYzAtMi4yMSwxLjc5LTQsNC00aDE4YzIuMjEsMCw0LDEuNzksNC" +
    "w0djM0YzAsMi4yMSwxLjc5LDQsNCw0aDM0YzIuMjEsMCw0LTEuNzksNC00VjEzYzAtMi4yMS" +
    "wxLjc5LTQsNC00aDE4YzIuMjEsMCw0LDEuNzksNCw0djEwMmMwLDIuMjEtMS43OSw0LTQsNG" +
    "gtMThjLTIuMjEsMC00LTEuNzktNC00di0zOWMwLTIuMjEtMS43OS00LTQtNGgtMzRjLTIuMi" +
    "wwLTQsMS44LTQsNHYzOWMwLDIuMjEtMS43OSw0LTQsNEgxNWMtMi4yMSwwLTQtMS43OS00LT" +
    "RaIi8+CiAgICA8ZyBmaWxsPSIjMjlhYWUyIj4KICAgICAgICA8cmVjdCB4PSI5MiIgeT0iOT" +
    "MiIHdpZHRoPSIyNiIgaGVpZ2h0PSIyNiIgcng9IjQiIHJ5PSI0Ii8+CiAgICAgICAgPHBhdG" +
    "ggZD0ibTkxLjUsMTNjLS4yLTIuMzUsMi4xNi00LDQuNTItNGgtLjAyczE3LjQ5LDAsMTcuND" +
    "ksMGMyLjM0LDAsNC4xOCwxLjk5LDMuOTksNC4zMi0xLjM0LDE2LjQtNC42Niw2NC4wNi00Lj" +
    "Y2LDY0LjA2LS4xOSwyLjA1LTEuOTEsMy42Mi0zLjk3LDMuNjJoLTguNTJjLTIuMDUsMC0zLj" +
    "c2LTEuNTUtMy45Ni0zLjU5LDAsMC0zLjQ3LTQ4LjMyLTQuODctNjQuNDEiLz4KICAgIDwvZz" +
    "4KPC9zdmc+Cg=="
