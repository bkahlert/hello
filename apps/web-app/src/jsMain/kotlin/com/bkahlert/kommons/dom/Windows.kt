package com.bkahlert.kommons.dom

import com.bkahlert.hello.url.URL
import com.bkahlert.hello.url.formUrlEncode
import io.ktor.http.Parameters
import org.w3c.dom.Location
import org.w3c.dom.Window


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
