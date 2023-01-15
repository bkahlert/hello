package com.bkahlert.kommons.dom

import org.w3c.dom.Element


/**
 * [data] gets arbitrary `data` attribute of the Element.
 */
public fun Element.data(dataAttr: String): String? = getAttribute("data-$dataAttr")

/**
 * [data] adds arbitrary `data` attribute to the Element.
 */
public fun Element.data(dataAttr: String, value: String): Unit = setAttribute("data-$dataAttr", value)
