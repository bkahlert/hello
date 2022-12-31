package com.bkahlert.aws.cdk

import com.bkahlert.kommons.text.groupValue

private val tokenUrlRegex = Regex("^https?://(?<domain>[^/]*)(?<path>.*?)/?$")

/** Domain name of this string representing a URL. */
val String.domain: String get() = replace(tokenUrlRegex) { checkNotNull(it.groupValue("domain")) }

/** Domain name of this string representing a URL. */
val String.path: String get() = replace(tokenUrlRegex) { checkNotNull(it.groupValue("path")) }
