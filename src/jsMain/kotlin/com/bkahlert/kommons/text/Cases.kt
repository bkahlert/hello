package com.bkahlert.kommons.text

/**
 * [Any.toString] variant that serializes instances of [CharSequence]
 * returned by [Cases].
 * TODO remove CharSequence based features or fix them by understanding what a [CharSequence] is supposed to provide (wrapping modification leaving original object untouched?)
 */
fun String.toStringWorkaround() = buildString { this@toStringWorkaround.forEach { append(it) } }

fun String.toWords() = Cases.camelCase.splitter(this).map { it.toStringWorkaround() }
fun String.toSentenceCaseString() = toWords().map { it.lowercase() }.joinLinesToString(" ")
fun String.toKebabCaseString() = toWords().let { Cases.`kebab-case`.joiner(it) }.toString()
