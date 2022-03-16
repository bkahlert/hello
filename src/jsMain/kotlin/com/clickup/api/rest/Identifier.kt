package com.clickup.api.rest

import com.bkahlert.kommons.web.http.div
import io.ktor.http.Url

interface Identifier<T> {
    val id: T
    val stringValue: String get() = id as? String ?: id.toString()
}

infix operator fun <T> Url.div(path: Identifier<T>): Url = div(path.id.toString())
