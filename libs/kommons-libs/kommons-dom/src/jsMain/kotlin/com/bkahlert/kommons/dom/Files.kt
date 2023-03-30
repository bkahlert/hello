package com.bkahlert.kommons.dom

import kotlinx.coroutines.await
import org.w3c.files.File
import kotlin.js.Promise

private fun File.text(): Promise<String> = asDynamic().text().unsafeCast<Promise<String>>()

public suspend fun File.readText(): String = text().await()
