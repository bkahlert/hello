package com.bkahlert.hello.components.applet

import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.reflect.KProperty

@Serializable(AppletSerializer::class)
interface Applet {
    @SerialName("id")
    val id: String

    @SerialName("title")
    @JsonNames("name")
    val title: String? get() = null

    @SerialName("icon")
    val icon: Uri? get() = null

    fun editor(isNew: Boolean): AppletEditor<*>

    fun render(renderContext: Tag<Element>): Tag<Element>

    fun RenderContext.renderConfigurationMissing(properties: List<KProperty<*>>, icon: Uri? = null): HtmlTag<HTMLDivElement> {
        val formattedKeys = when (properties.size) {
            0 -> ""
            1, 2 -> properties.joinToString(" and ") { it.name } + " "
            else -> properties.take(properties.size - 1).joinToString(", ") { it.name } + ", and " + properties.last().name + " "
        } + "configuration missing"
        return div("grid items-center justify-stretch") {
            div("flex py-5 items-center text-md opacity-60 font-semibold select-none") {
                div("flex-grow border-b border-current") { }
                div("flex-shrink mx-4 flex gap-2 items-center") {
                    icon?.also { icon("w-4 h-4", it) }
                    span("uppercase") { +formattedKeys }
                }
                div("flex-grow border-b border-current") { }
            }
        }
    }

    fun RenderContext.renderConfigurationMissing(vararg properties: KProperty<*>, icon: Uri? = null) =
        renderConfigurationMissing(properties.asList(), icon)

    companion object {
        fun randomId(): String = buildString {
            fun randomChar(): Char = ('a'..'z').random()
            repeat(3) { append(randomChar()) }
            append('-')
            repeat(3) { append(randomChar()) }
        }
    }
}
