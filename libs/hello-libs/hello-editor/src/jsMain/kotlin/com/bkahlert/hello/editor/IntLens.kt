package com.bkahlert.hello.editor

import dev.fritz2.core.Lens

public object IntLens : Lens<Int?, String> {
    override val id: String get() = ""
    override fun get(parent: Int?): String = parent?.toString().orEmpty()
    override fun set(parent: Int?, value: String): Int? = value.toIntOrNull()
}
