package com.bkahlert.kommons.net

import io.ktor.http.Url

/**
 * Returns the [Uri] converted to a [Url].
 */
public fun Uri.toUrl(): Url = Url(toString())
