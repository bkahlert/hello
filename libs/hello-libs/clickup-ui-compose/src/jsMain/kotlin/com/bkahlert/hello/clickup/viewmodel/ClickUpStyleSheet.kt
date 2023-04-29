package com.bkahlert.hello.clickup.viewmodel

import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.semanticui.core.Device
import com.bkahlert.semanticui.core.mediaMaxDeviceWidth
import io.ktor.http.ContentType.Image
import org.jetbrains.compose.web.css.CSSMediaQuery.MediaType
import org.jetbrains.compose.web.css.CSSMediaQuery.Only
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.maxHeight
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgba
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.width

@Suppress("PublicApiImplicitType")
public object ClickUpStyleSheet : StyleSheet() {
    private fun roundedMask(size: Int = 512) = DataUri(
        Image.SVG,
        //language=SVG
        """
            <svg xmlns="http://www.w3.org/2000/svg" width="$size" height="$size">
                <circle cx="${size / 2}" cy="${size / 2}" r="${size / 2}" fill="#ffffff"/>
            </svg>
        """.trimIndent()
    )

    init {
        "style + .ui.menu" style {
            marginTop(0.px)
            marginBottom(0.px)
        }

        "img.rounded" style {
            val maskImage = "url('${roundedMask()}')"
            val maskPosition = "0 0"
            val maskSize = "100%"
            val maskRepeat = "no-repeat"
            property("-webkit-mask", "$maskImage $maskPosition/$maskSize $maskRepeat")
            property("mask", "$maskImage $maskPosition/$maskSize $maskRepeat")
            property("mask-image", maskImage)
            property("mask-position", maskPosition)
            property("mask-size", maskSize)
            property("mask-repeat", maskRepeat)
        }

        ".ui.menu:first-child:last-child" style {
            marginTop(0.px)
        }

        ".ui.menu .item > img:not(.ui).mini" style {
            width(2.2.em)
            maxHeight(1.6.em)
            margin((-.18).em, 0.em)
        }
        ".ui.menu .item.link > img:not(.ui)" style {
            marginRight(0.25.em)
        }

        ".ui.menu .item > img:not(.ui).avatar" style {
            width(2.2.em)
            maxHeight(2.2.em)
            margin((-.5).em, 0.em)
        }

        // fix: nested basic modals (with default context) basically disappear
        //      since there is no dimmer between them and their parent modal
        ".ui.modal.active + .ui.modal.basic" style {
            backgroundColor(rgba(0, 0, 0, .75))
            borderRadius(0.25.cssRem)
        }

        // reduce padding on small devices
        media(mediaMaxDeviceWidth(Device.Mobile)) {
            ".ui.menu:not(.vertical) > .item + .item" style {
                property("padding-left", "0")
            }
        }

        // hide meta information on small devices
        media(mediaMaxDeviceWidth(Device.Tablet)) {
            ".ui.menu:not(.vertical) .right.meta.menu" style {
                property("display", "none")
                property("outline", "1px solid blue")
            }
        }

        // use 50% of the available screen height for the task dropdown
        Device.values().forEach { device ->
            media(Only(MediaType(MediaType.Enum.Screen), device.maxWidthMediaFeature)) {
                ".ui.dropdown .scrolling.menu, .ui.scrolling.dropdown .menu" style {
                    maxHeight(50.vh)
                }
            }
        }
    }
}
