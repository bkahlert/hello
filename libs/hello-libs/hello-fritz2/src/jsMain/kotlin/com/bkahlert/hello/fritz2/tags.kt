@file:Suppress("NOTHING_TO_INLINE")

package com.bkahlert.hello.fritz2

import dev.fritz2.core.Tag
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Element

public typealias ContentBuilder<C> = Tag<C>.() -> Unit
public typealias ContentBuilder1<C, T> = Tag<C>.(T) -> Unit
public typealias ContentBuilder2<C, T, U> = Tag<C>.(T, U) -> Unit

/** Sets a `data` attribute. */
public inline fun Tag<Element>.data(name: String, value: String): Unit = attr("data-$name", value)

/** Sets a `data` attribute only if its [value] is not null. */
public inline fun Tag<Element>.data(name: String, value: String?): Unit = attr("data-$name", value)

/** Sets a `data` attribute. */
public inline fun Tag<Element>.data(name: String, value: Flow<String>): Unit = attr("data-$name", value)

/** Sets a `data` attribute only for all non-null values of the flow. */
public inline fun Tag<Element>.data(name: String, value: Flow<String?>): Unit = attr("data-$name", value)
