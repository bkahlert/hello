package com.bkahlert.hello.semanticui

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.semanticui.custom.Length
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px

/** Semantic UI page variables */
object Page {
    inline val PageBackground: Color get() = RGB(0xFFFFFF)
    inline val LineHeight: Length get() = 1.4285.em
    inline val TextColor: Color get() = RGB(0, 0, 0, 0.87)
}

/** Semantic UI paragraph variables */
object Paragraph {
    inline val ParagraphMargin: Array<out Length> get() = arrayOf(0.em, 0.em, 1.em)
    inline val ParagraphLineHeight: Length get() = Page.LineHeight
}

/** Semantic UI link variables */
object Links {
    inline val LinkColor: Color get() = RGB(0x4183C4)
    inline val LinkUnderline: String get() = "none"

    //inline val LinkHoverColor get()      : Color = LinkColor.saturate(.2).darken(.15)
    inline val LinkHoverUnderline: String get() = LinkUnderline
}

/** Semantic UI highlighted text variables */
object HighlightedText {
    inline val HighlightBackground: Color get() = RGB(0xCCE2FF)
    inline val HighlightColor: Color get() = Page.TextColor

    inline val InputHighlightBackground: Color get() = RGB(100, 100, 100, 0.4)
    inline val InputHighlightColor: Color get() = Page.TextColor
}

/** Semantic UI breakpoint variables */
object BreakPoints {
    inline val MobileBreakpoint: Length get() = 320.px
    inline val TabletBreakpoint: Length get() = 768.px
    inline val ComputerBreakpoint: Length get() = 992.px
    inline val LargeMonitorBreakpoint: Length get() = 1200.px
    inline val WidescreenMonitorBreakpoint: Length get() = 1920.px
}

/** Semantic UI site color variables */
object SiteColors {

    inline val Red: Color get() = RGB(0xDB2828)
    inline val Orange: Color get() = RGB(0xF2711C)
    inline val Yellow: Color get() = RGB(0xFBBD08)
    inline val Olive: Color get() = RGB(0xB5CC18)
    inline val Green: Color get() = RGB(0x21BA45)
    inline val Teal: Color get() = RGB(0x00B5AD)
    inline val Blue: Color get() = RGB(0x2185D0)
    inline val Violet: Color get() = RGB(0x6435C9)
    inline val Purple: Color get() = RGB(0xA333C8)
    inline val Pink: Color get() = RGB(0xE03997)
    inline val Brown: Color get() = RGB(0xA5673F)
    inline val Grey: Color get() = RGB(0x767676)
    inline val Black: Color get() = RGB(0x1B1C1D)

    inline val LightRed: Color get() = RGB(0xFF695E)
    inline val LightOrange: Color get() = RGB(0xFF851B)
    inline val LightYellow: Color get() = RGB(0xFFE21F)
    inline val LightOlive: Color get() = RGB(0xD9E778)
    inline val LightGreen: Color get() = RGB(0x2ECC40)
    inline val LightTeal: Color get() = RGB(0x6DFFFF)
    inline val LightBlue: Color get() = RGB(0x54C8FF)
    inline val LightViolet: Color get() = RGB(0xA291FB)
    inline val LightPurple: Color get() = RGB(0xDC73FF)
    inline val LightPink: Color get() = RGB(0xFF8EDF)
    inline val LightBrown: Color get() = RGB(0xD67C1C)
    inline val LightGrey: Color get() = RGB(0xDCDDDE)
    inline val LightBlack: Color get() = RGB(0x545454)

    inline val FullBlack: Color get() = RGB(0x000000)
    inline val OffWhite: Color get() = RGB(0xF9FAFB)
    inline val DarkWhite: Color get() = RGB(0xF3F4F5)
    inline val MidWhite: Color get() = RGB(0xDCDDDE)
    inline val White: Color get() = RGB(0xFFFFFF)

    inline val RedBackground: Color get() = RGB(0xFFE8E6)
    inline val OrangeBackground: Color get() = RGB(0xFFEDDE)
    inline val YellowBackground: Color get() = RGB(0xFFF8DB)
    inline val OliveBackground: Color get() = RGB(0xFBFDEF)
    inline val GreenBackground: Color get() = RGB(0xE5F9E7)
    inline val TealBackground: Color get() = RGB(0xE1F7F7)
    inline val BlueBackground: Color get() = RGB(0xDFF0FF)
    inline val VioletBackground: Color get() = RGB(0xEAE7FF)
    inline val PurpleBackground: Color get() = RGB(0xF6E7FF)
    inline val PinkBackground: Color get() = RGB(0xFFE3FB)
    inline val BrownBackground: Color get() = RGB(0xF1E2D3)

    inline val RedHeaderColor get() = RedTextColor.toHSL().darken(.05)
    inline val OliveHeaderColor get() = OliveTextColor.toHSL().darken(.05)
    inline val GreenHeaderColor get() = GreenTextColor.toHSL().darken(.05)
    inline val YellowHeaderColor get() = YellowTextColor.toHSL().darken(.05)
    inline val BlueHeaderColor get() = BlueTextColor.toHSL().darken(.05)
    inline val TealHeaderColor get() = TealTextColor.toHSL().darken(.05)
    inline val PinkHeaderColor get() = PinkTextColor.toHSL().darken(.05)
    inline val VioletHeaderColor get() = VioletTextColor.toHSL().darken(.05)
    inline val PurpleHeaderColor get() = PurpleTextColor.toHSL().darken(.05)
    inline val OrangeHeaderColor get() = OrangeTextColor.toHSL().darken(.05)
    inline val BrownHeaderColor get() = BrownTextColor.toHSL().darken(.05)

    inline val RedTextColor: Color get() = Red
    inline val OrangeTextColor: Color get() = Orange
    inline val YellowTextColor: Color get() = RGB(0xB58105)
    inline val OliveTextColor: Color get() = RGB(0x8ABC1E)
    inline val GreenTextColor: Color get() = RGB(0x1EBC30)
    inline val TealTextColor: Color get() = RGB(0x10A3A3)
    inline val BlueTextColor: Color get() = Blue
    inline val VioletTextColor: Color get() = Violet
    inline val PurpleTextColor: Color get() = Purple
    inline val PinkTextColor: Color get() = Pink
    inline val BrownTextColor: Color get() = Brown

    inline val RedBorderColor: Color get() = RedTextColor
    inline val OrangeBorderColor: Color get() = OrangeTextColor
    inline val YellowBorderColor: Color get() = YellowTextColor
    inline val OliveBorderColor: Color get() = OliveTextColor
    inline val GreenBorderColor: Color get() = GreenTextColor
    inline val TealBorderColor: Color get() = TealTextColor
    inline val BlueBorderColor: Color get() = BlueTextColor
    inline val VioletBorderColor: Color get() = VioletTextColor
    inline val PurpleBorderColor: Color get() = PurpleTextColor
    inline val PinkBorderColor: Color get() = PinkTextColor
    inline val BrownBorderColor: Color get() = BrownTextColor

    inline val SubtleTransparentBlack: Color get() = RGB(0, 0, 0, 0.03)
    inline val TransparentBlack: Color get() = RGB(0, 0, 0, 0.05)
    inline val StrongTransparentBlack: Color get() = RGB(0, 0, 0, 0.10)
    inline val VeryStrongTransparentBlack: Color get() = RGB(0, 0, 0, 0.15)

    inline val SubtleTransparentWhite: Color get() = RGB(255, 255, 255, 0.02)
    inline val TransparentWhite: Color get() = RGB(255, 255, 255, 0.08)
    inline val StrongTransparentWhite: Color get() = RGB(255, 255, 255, 0.15)
}
