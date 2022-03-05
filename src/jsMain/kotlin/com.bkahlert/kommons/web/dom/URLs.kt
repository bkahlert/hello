package com.bkahlert.kommons.web.dom

import org.w3c.dom.url.URL

fun CharSequence.toUrl(): URL =
    this.toString().let { kotlin.runCatching { URL(it) } }.getOrThrow()

fun CharSequence?.toUrlOrNull(): URL? =
    this?.toString()?.let { kotlin.runCatching { URL(it) } }?.getOrNull()
