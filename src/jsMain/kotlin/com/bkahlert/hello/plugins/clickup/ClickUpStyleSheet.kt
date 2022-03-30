package com.bkahlert.hello.plugins.clickup

import com.bkahlert.kommons.SVGImage
import org.jetbrains.compose.web.css.CSSStyleRuleBuilder
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgba
import org.jetbrains.compose.web.css.width

@Suppress("PublicApiImplicitType")
object ClickUpStyleSheet : StyleSheet() {
    private val size = 512
    private val profilePictureMask = SVGImage(
        //language=SVG
        """
            <svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="$size" height="$size">
                <circle cx="${size / 2}" cy="${size / 2}" r="${size / 2}" fill="#ffffff"/>
            </svg>
        """.trimIndent())

    private fun CSSStyleRuleBuilder.miniMenuImage() {
        width(2.2.em)
        margin((-.5).em, 0.em);
    }

    init {
        ".ui.menu:first-child:last-child" style {
            marginTop(0.px)
        }

        ".ui.menu .item > img:not(.ui).mini" style {
            miniMenuImage()
        }
        ".ui.menu .item.link > img:not(.ui)" style {
            marginRight(0.25.em)
        }

        ".ui.menu .item > img:not(.ui).avatar" style {
            property("mask-image", """url('${profilePictureMask.dataURI}')""")
            property("mask-position", "center")
            property("mask-size", "100%")
            property("mask-repeat", "no-repeat")
            miniMenuImage()
        }

        // fix: nested basic modals (with default context) basically disappear
        //      since there is no dimmer between them and their parent modal
        ".ui.modal.active + .ui.modal.basic" style {
            backgroundColor(rgba(0, 0, 0, .75))
            borderRadius(0.25.cssRem)
        }
    }
}
