package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.toString
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.element.Loader
import com.bkahlert.semanticui.element.LoaderElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Iframe
import org.w3c.dom.HTMLIFrameElement
import org.w3c.dom.events.EventListener

/**
 * Renders an [Iframe] created with the specified [frameAttrs]—showing
 * a [Loader] with the optional [text] until the iFrame loaded.
 */
@Composable
@Suppress("NOTHING_TO_INLINE")
public inline fun IFrame(
    text: String? = null,
    noinline attrs: SemanticAttrBuilderContext<LoaderElement>? = null,
    noinline frameAttrs: AttrBuilderContext<HTMLIFrameElement>? = null,
) {
    val logger = remember { ConsoleLogger("IFrame") }
    var loaded by remember { mutableStateOf(false) }

    Div({
        classes("ui", "dimmable")
        if (!loaded) classes("loading")
        classes("iframe")
    }) {
        Loader(text, attrs)
        Iframe(frameAttrs) {
            DisposableEffect(Unit) {
                val loadEventListener = EventListener {
                    logger.info("Loading finished", it)
                    loaded = true
                }
                loadEventListener.toString { "LoadEventListener" }
                scopeElement.addEventListener("load", loadEventListener)
                logger.info("Added $loadEventListener for", scopeElement)
                onDispose {
                    scopeElement.removeEventListener("load", loadEventListener)
                    logger.info("Removed $loadEventListener from", scopeElement)
                }
            }
        }
    }
}


/** Sets [HTMLIFrameElement.src] to the specified [url]. */
public fun AttrsScope<HTMLIFrameElement>.src(url: CharSequence) {
    attr("src", url.toString())
}

/** Sets [HTMLIFrameElement.sandbox] to the specified [permissions]. */
public fun AttrsScope<HTMLIFrameElement>.sandbox(vararg permissions: Sandbox) {
    attr("sandbox", permissions.joinToString(" "))
}

/** Possible values for [HTMLIFrameElement.sandbox]. */
public enum class Sandbox(private val value: String) {
    /** Allows form submission. */
    ALLOW_FORMS("allow-forms"),

    /** Allows opening modal windows. */
    ALLOW_MODALS("allow-modals"),

    /** Allows locking the screen orientation. */
    ALLOW_ORIENTATION_LOCK("allow-orientation-lock"),

    /** Allows using the Pointer Lock API. */
    ALLOW_POINTER_LOCK("allow-pointer-lock"),

    /** Allows popups. */
    ALLOW_POPUPS("allow-popups"),

    /** Allows popups to open new windows without inheriting the sandboxing. */
    ALLOW_POPUPS_TO_ESCAPE_SANDBOX("allow-popups-to-escape-sandbox"),

    /** Allows starting a presentation session. */
    ALLOW_PRESENTATION("allow-presentation"),

    /** Allows the iframe content to be treated as being from the same origin. */
    ALLOW_SAME_ORIGIN("allow-same-origin"),

    /** Allows running scripts. */
    ALLOW_SCRIPTS("allow-scripts"),

    /** Allows the iframe content to navigate its top-level browsing context. */
    ALLOW_TOP_NAVIGATION("allow-top-navigation"),

    /** Allows the iframe content to navigate its top-level browsing context, but only if initiated by user. */
    ALLOW_TOP_NAVIGATION_BY_USER_ACTIVATION("allow-top-navigation-by-user-activation"),
    ;

    override fun toString(): String = value
}
