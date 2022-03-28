package com.bkahlert.kommons.collections

inline val <K, V> Map<K, V>.pairs get() = entries.map { (l, r) -> l to r }
inline val <K, V> Map<K, V>.pairArray get() = pairs.toTypedArray()
