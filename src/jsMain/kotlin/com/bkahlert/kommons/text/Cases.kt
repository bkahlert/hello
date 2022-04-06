package com.bkahlert.kommons.text

/**
 * [Any.toString] variant that serializes instances of [CharSequence]
 * returned by [Cases].
 * TODO remove CharSequence based features or fix them by understanding what a [CharSequence] is supposed to provide (wrapping modification leaving original object untouched?)
 */
fun CharSequence.toStringWorkaround() = buildString { this@toStringWorkaround.forEach { append(it) } }

fun CharSequence.toWords() = Cases.camelCase.splitter(this).map { it.toStringWorkaround() }
fun CharSequence.toSentenceCaseString() = toWords().map { it.lowercase() }.joinLinesToString(" ")
fun CharSequence.toKebabCaseString() = toWords().let { Cases.kebabcase.joiner(it) }.toString()
