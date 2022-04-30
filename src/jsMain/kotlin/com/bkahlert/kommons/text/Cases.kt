package com.bkahlert.kommons.text

import com.bkahlert.kommons.text.Cases.`kebab-case`

fun String.toSentenceCaseString() = splitCamelCase().map { it.lowercase() }.joinLinesToString(" ")
fun String.toKebabCaseString(): String = splitCamelCase().let { `kebab-case`.joiner(it) }.toString()
