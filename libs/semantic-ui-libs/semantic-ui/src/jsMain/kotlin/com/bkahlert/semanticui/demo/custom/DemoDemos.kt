package com.bkahlert.semanticui.demo.custom

import androidx.compose.runtime.Composable
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.TextAlignment
import com.bkahlert.semanticui.demo.Demo
import com.bkahlert.semanticui.demo.Demos
import com.bkahlert.semanticui.demo.LoremIpsumParagraph
import com.bkahlert.semanticui.element.Header
import com.bkahlert.semanticui.element.SubHeader
import com.bkahlert.semanticui.element.aligned
import com.bkahlert.semanticui.element.padded
import org.jetbrains.compose.web.dom.Text

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

public class SemanticDemo(
    public val name: String,
    public vararg val sections: SemanticDemoSection,
) {
    @Composable public operator fun invoke() {
        Header { Text(name) }
        sections.forEach { section ->
            SubHeader { Text(section.name) }
            section.content()
        }
    }
}
