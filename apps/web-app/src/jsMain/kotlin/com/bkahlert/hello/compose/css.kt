package com.bkahlert.hello.compose

import org.jetbrains.compose.web.css.CSSSizeValue
import org.jetbrains.compose.web.css.CSSUnitLength

/** CSS length, e.g. `42.em` */
typealias Length = CSSSizeValue<out CSSUnitLength>
