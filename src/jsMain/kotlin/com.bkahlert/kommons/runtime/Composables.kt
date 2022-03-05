package com.bkahlert.kommons.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.bkahlert.kommons.text.randomString

/**
 * Remember the value produced by [calculation]. [calculation] will only be evaluated during the composition.
 * Recomposition will always return the value produced by composition.
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun id(prefix: String? = null): String =
    remember { mutableStateOf("${prefix?.let { "$it--" } ?: ""}${randomString()}") }.value
