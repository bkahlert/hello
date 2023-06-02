package com.bkahlert.hello.editor


// TODO move to kommons or refactor / rename or inline

public fun <T> List<T>.move(index: Int, offset: Int): List<T> {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index out of bounds: $index")
    val newIndex = (index + offset).coerceIn(indices)
    return toMutableList().apply { add(newIndex, removeAt(index)) }
}

public fun <T> List<T>.move(element: T, offset: Int): List<T> =
    indexOfFirst { it == element }
        .takeUnless { it < 0 }
        ?.let { move(it, offset) }
        ?: this
