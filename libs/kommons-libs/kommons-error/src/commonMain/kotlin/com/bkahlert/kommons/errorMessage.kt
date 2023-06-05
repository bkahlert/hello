package com.bkahlert.kommons

public val Throwable.errorMessage: String get() = message ?: this::class.simpleName ?: "An unknown problem has occurred"
