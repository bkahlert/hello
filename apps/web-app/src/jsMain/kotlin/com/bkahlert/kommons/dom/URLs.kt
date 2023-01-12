package com.bkahlert.kommons.dom

import io.ktor.http.URLBuilder
import io.ktor.http.Url

inline operator fun Url.invoke(builder: URLBuilder.() -> Unit): Url = URLBuilder(toString()).apply(builder).build()

infix operator fun Url.div(path: String): Url = invoke { pathSegments = pathSegments + path }
