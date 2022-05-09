package com.bkahlert.kommons.dom

import io.ktor.http.Parameters
import org.w3c.dom.Location
import org.w3c.dom.Window

inline fun Window.open(
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

/** The [URL] of `this` location. */
var Location.url: URL
    get() = URL.parse(href)
    set(value) {
        href = value.toString()
    }

/** The query [Parameters] of `this` location. */
var Location.query: Parameters
    get() = url.query
    set(value) {
        search = value.formUrlEncode()
    }

/** The hash/fragment [Parameters] of `this` location. */
var Location.fragment: Parameters
    get() = url.fragment
    set(value) {
        hash = value.formUrlEncode()
    }
