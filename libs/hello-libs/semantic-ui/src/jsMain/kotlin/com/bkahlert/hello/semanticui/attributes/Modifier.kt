package com.bkahlert.hello.semanticui.attributes

public interface Modifier {
    public val classNames: Array<out String>

    public companion object {
        public fun of(vararg classNames: String): Modifier = object : Modifier {
            override val classNames: Array<out String> get() = classNames
        }
    }
}

public operator fun Modifier.plus(other: Modifier): Modifier = Modifier.of(*classNames, *other.classNames)
public inline val Array<out Modifier>.classNames: Array<out String>
    get() = flatMap { it.classNames.asIterable() }.toTypedArray()
