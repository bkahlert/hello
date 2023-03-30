package com.bkahlert.hello.fritz2

import dev.fritz2.core.Keys
import dev.fritz2.core.Shortcut
import dev.fritz2.core.Tag
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.w3c.dom.Element

public fun Shortcut.describe(): String = buildString {
    val apple = with(window.navigator.userAgent) { contains("Macintosh") || contains("Mac OS X") || contains("iOS") }
    if (ctrl) append(if (apple) "⌃ " else "Ctrl+")
    if (alt) append(if (apple) "⌥ " else "Alt+")
    if (shift) append(if (apple) "⇧ " else "Shift+")
    if (meta) append(if (apple) "⌘ " else "Meta+")
    when (key) {
        Keys.Enter.key -> append("⏎")
        Keys.Escape.key -> append("⎋")
        Keys.Space.key -> append(if (apple) "␣" else "Space")
        else -> append(key.uppercase())
    }
}

/** Sets a `data-shortcut` attribute.*/
public fun Tag<Element>.shortcut(value: Shortcut): Unit = data("shortcut", value.describe())

/** Sets a `data-shortcut` attribute only if its [value] is not null. */
public fun Tag<Element>.shortcut(value: Shortcut?): Unit = data("shortcut", value?.describe())

/** Sets a `data-shortcut` attribute. */
public fun Tag<Element>.shortcut(value: Flow<Shortcut>): Unit = data("shortcut", value.map { it.describe() })

/** Sets a `data-shortcut` attribute only for all non-null values of the flow. */
public fun Tag<Element>.shortcut(value: Flow<Shortcut?>): Unit = data("shortcut", value.map { it?.describe() })
