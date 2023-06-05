package com.bkahlert.kommons

import kotlin.random.Random

/** Creates a random string of the specified [length] made up of the specified [allowedCharacters]. */
public fun Random.string(length: Int = 16, vararg allowedCharacters: Char = (('0'..'9') + ('a'..'z') + ('A'..'Z')).toCharArray()): String =
    buildString(length) { repeat(length) { append(allowedCharacters[nextInt(0, allowedCharacters.size)]) } }
