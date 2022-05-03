package com.bkahlert.kommons.dom

import io.ktor.http.Parameters
import io.ktor.http.Url
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

/**
 * Contains the [Url] of this location.
 */
var Location.url: URL
    get() = URL.parse(href)
    set(value) {
        href = value.toString()
    }

/**
 * Query parameters
 */
var Location.parameters: Parameters
    get() = url.parameters
    set(value) {
        search = value.formUrlEncode()
    }

/**
 * Hash / fragment parameters
 */
var Location.fragmentParameters: Parameters
    get() = url.fragmentParameters
    set(value) {
        hash = value.formUrlEncode()
    }
