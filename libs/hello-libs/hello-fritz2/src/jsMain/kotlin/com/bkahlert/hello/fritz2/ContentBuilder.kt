package com.bkahlert.hello.fritz2

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext

public typealias ContentBuilder = RenderContext.() -> Unit
public typealias HtmlContentBuilder<T> = HtmlTag<T>.() -> Unit
