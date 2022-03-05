package com.bkahlert.kommons.browser

import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.dom.url.URL
import kotlin.time.Duration

/**
 * Executes the specified [block] with the given [delay].
 */
fun delayed(delay: Duration, block: () -> Unit) {
    window.setTimeout(block, delay.inWholeMilliseconds.toInt())
}

inline fun Window.open(
    url: URL,
    target: String? = null,
    features: String? = null,
): Window? =
    url.toString().let { stringUrl ->
        target?.let {
            open(stringUrl, target)
            features?.let {
                open(stringUrl, target, features)
            }
        } ?: open(stringUrl)
    }

inline fun Window.openInSameTab(
    url: URL,
    features: String? = null,
    newTabFallback: Boolean = true,
): Window? =
    runCatching {
        open(url, "_top", features)
    }.onFailure {
        if (newTabFallback) openInNewTab(url, features)
        else throw it
    }.getOrThrow()

inline fun Window.openInNewTab(
    url: URL,
    features: String? = null,
): Window? = open(url, "_blank", features)
