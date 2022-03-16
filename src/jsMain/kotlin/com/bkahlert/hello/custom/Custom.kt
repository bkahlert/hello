package com.bkahlert.hello.custom

import androidx.compose.runtime.Composable
import com.bkahlert.Brand.colors
import com.bkahlert.hello.Spinner
import com.bkahlert.hello.custom.Sandbox.ALLOW_POPUPS
import com.bkahlert.hello.custom.Sandbox.ALLOW_SAME_ORIGIN
import com.bkahlert.hello.custom.Sandbox.ALLOW_SCRIPTS
import com.bkahlert.hello.custom.Sandbox.ALLOW_TOP_NAVIGATION
import com.bkahlert.hello.custom.Sandbox.ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION
import com.bkahlert.hello.custom.Sandbox.Companion.sandbox
import com.bkahlert.kommons.backgroundImage
import com.bkahlert.kommons.text.joinLinesToString
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.CSSBuilder
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.opacity
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Iframe
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.url.URL

// TODO https://semantic-ui.com/modules/embed.html

@Composable
fun Custom(
    url: URL? = null,
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Style(CustomStyleSheet)
    Div({
        classes(CustomStyleSheet.custom, CustomStyleSheet.loading, CustomStyleSheet.loaded)
        attrs?.invoke(this)
    }) {
        url?.also {
            Iframe({
                src(it)
                sandbox(
                    ALLOW_POPUPS,
                    ALLOW_SCRIPTS,
                    ALLOW_SAME_ORIGIN,
                    ALLOW_TOP_NAVIGATION,
                    ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION,
                )
            })
        }
    }
}

object CustomStyleSheet : StyleSheet() {

    val scale = 0.75

    val custom by style {
        height(100.percent)
        child(self, type("*")) style {
            width((100 / scale).percent)
            height((100 / scale).percent)
            property("zoom", "$scale");
            property("-moz-transform", "scale($scale)");
            property("-moz-transform-origin", "0 0");
            property("-o-transform", "scale($scale)");
            property("-o-transform-origin", "0 0");
            property("-webkit-transform", "scale($scale)");
            property("-webkit-transform-origin", "0 0");
            property("border", "none")
        }
    }

    val loading by style {
        spinner()
        child(self, type("*")) style {
            opacity(0)
            backgroundRepeat("no-repeat")
            backgroundPosition("center center")
        }
    }

    val loaded by style {
        spinner()
        child(self, type("*")) style {
            property("transition", "opacity .4s ease-in")
            opacity(1)
        }
    }
}

@OptIn(ExperimentalComposeWebApi::class)
fun CSSBuilder.spinner() {
    backgroundImage(Spinner(colors.black.transparentize(.67)))
    backgroundRepeat("no-repeat")
    backgroundPosition("center center")
}

fun AttrsScope<HTMLIFrameElement>.src(url: URL) {
    attr("src", url.toString())
}

enum class Sandbox {
    /** Allows form submission. */
    ALLOW_FORMS,

    /** Allows to open modal windows. */
    ALLOW_MODALS,

    /** Allows to lock the screen orientation. */
    ALLOW_ORIENTATION_LOCK,

    /** Allows to use the Pointer Lock API. */
    ALLOW_POINTER_LOCK,

    /** Allows popups. */
    ALLOW_POPUPS,

    /** Allows popups to open new windows without inheriting the sandboxing. */
    ALLOW_POPUPS_TO_ESCAPE_SANDBOX,

    /** Allows to start a presentation session. */
    ALLOW_PRESENTATION,

    /** Allows the iframe content to be treated as being from the same origin. */
    ALLOW_SAME_ORIGIN,

    /** Allows to run scripts. */
    ALLOW_SCRIPTS,

    /** Allows the iframe content to navigate its top-level browsing context. */
    ALLOW_TOP_NAVIGATION,

    /** Allows the iframe content to navigate its top-level browsing context, but only if initiated by user. */
    ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION,
    ;

    companion object {
        fun AttrsScope<HTMLIFrameElement>.sandbox(vararg permissions: Sandbox) {
            attr("sandbox", permissions.joinLinesToString(" ") { it.name.lowercase().replace('_', '-') })
        }
    }
}
