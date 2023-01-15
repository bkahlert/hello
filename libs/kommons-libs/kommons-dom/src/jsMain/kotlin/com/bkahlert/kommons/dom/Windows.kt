package com.bkahlert.kommons.dom

import org.w3c.dom.Window
import org.w3c.dom.url.URL

public inline fun Window.open(
    url: URL,
    target: String? = null,
    features: String? = null,
): Window? {
    val urlString = url.toString()
    return if (target != null) {
        if (features != null) {
            open(urlString, target, features)
        } else {
            open(urlString, target)
        }
    } else {
        open(urlString)
    }
}

public inline fun Window.openInSameTab(
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

public inline fun Window.openInNewTab(
    url: URL,
    features: String? = null,
): Window? = open(url, "_blank", features)
