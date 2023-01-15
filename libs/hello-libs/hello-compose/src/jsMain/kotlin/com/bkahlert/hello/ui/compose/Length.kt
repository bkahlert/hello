package com.bkahlert.hello.ui.compose

import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength

/** CSS length, e.g. `42.em` */
public typealias Length = CSSSizeValue<out CSSUnitLength>
