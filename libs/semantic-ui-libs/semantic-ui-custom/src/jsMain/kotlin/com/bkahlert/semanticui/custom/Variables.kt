package com.bkahlert.semanticui.custom

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px

/** Semantic UI page variables */
public object Page {
    public inline val PageBackground: Color get() = RGB(0xFFFFFF)
    public inline val LineHeight: Length get() = 1.4285.em
    public inline val TextColor: Color get() = RGB(0, 0, 0, 0.87)
}

/** Semantic UI paragraph variables */
public object Paragraph {
    public inline val ParagraphMargin: Array<out Length> get() = arrayOf(0.em, 0.em, 1.em)
    public inline val ParagraphLineHeight: Length get() = Page.LineHeight
}

/** Semantic UI link variables */
public object Links {
    public inline val LinkColor: Color get() = RGB(0x4183C4)
    public inline val LinkUnderline: String get() = "none"

    //inline val LinkHoverColor get()      : Color = LinkColor.saturate(.2).darken(.15)
    public inline val LinkHoverUnderline: String get() = LinkUnderline
}

/** Semantic UI highlighted text variables */
public object HighlightedText {
    public inline val HighlightBackground: Color get() = RGB(0xCCE2FF)
    public inline val HighlightColor: Color get() = Page.TextColor

    public inline val InputHighlightBackground: Color get() = RGB(100, 100, 100, 0.4)
    public inline val InputHighlightColor: Color get() = Page.TextColor
}

/** Semantic UI breakpoint variables */
public object BreakPoints {
    public inline val MobileBreakpoint: Length get() = 320.px
    public inline val TabletBreakpoint: Length get() = 768.px
    public inline val ComputerBreakpoint: Length get() = 992.px
    public inline val LargeMonitorBreakpoint: Length get() = 1200.px
    public inline val WidescreenMonitorBreakpoint: Length get() = 1920.px
}

/** Semantic UI site color variables */
public object SiteColors {

    public inline val Red: Color get() = RGB(0xDB2828)
    public inline val Orange: Color get() = RGB(0xF2711C)
    public inline val Yellow: Color get() = RGB(0xFBBD08)
    public inline val Olive: Color get() = RGB(0xB5CC18)
    public inline val Green: Color get() = RGB(0x21BA45)
    public inline val Teal: Color get() = RGB(0x00B5AD)
    public inline val Blue: Color get() = RGB(0x2185D0)
    public inline val Violet: Color get() = RGB(0x6435C9)
    public inline val Purple: Color get() = RGB(0xA333C8)
    public inline val Pink: Color get() = RGB(0xE03997)
    public inline val Brown: Color get() = RGB(0xA5673F)
    public inline val Grey: Color get() = RGB(0x767676)
    public inline val Black: Color get() = RGB(0x1B1C1D)

    public inline val LightRed: Color get() = RGB(0xFF695E)
    public inline val LightOrange: Color get() = RGB(0xFF851B)
    public inline val LightYellow: Color get() = RGB(0xFFE21F)
    public inline val LightOlive: Color get() = RGB(0xD9E778)
    public inline val LightGreen: Color get() = RGB(0x2ECC40)
    public inline val LightTeal: Color get() = RGB(0x6DFFFF)
    public inline val LightBlue: Color get() = RGB(0x54C8FF)
    public inline val LightViolet: Color get() = RGB(0xA291FB)
    public inline val LightPurple: Color get() = RGB(0xDC73FF)
    public inline val LightPink: Color get() = RGB(0xFF8EDF)
    public inline val LightBrown: Color get() = RGB(0xD67C1C)
    public inline val LightGrey: Color get() = RGB(0xDCDDDE)
    public inline val LightBlack: Color get() = RGB(0x545454)

    public inline val FullBlack: Color get() = RGB(0x000000)
    public inline val OffWhite: Color get() = RGB(0xF9FAFB)
    public inline val DarkWhite: Color get() = RGB(0xF3F4F5)
    public inline val MidWhite: Color get() = RGB(0xDCDDDE)
    public inline val White: Color get() = RGB(0xFFFFFF)

    public inline val RedBackground: Color get() = RGB(0xFFE8E6)
    public inline val OrangeBackground: Color get() = RGB(0xFFEDDE)
    public inline val YellowBackground: Color get() = RGB(0xFFF8DB)
    public inline val OliveBackground: Color get() = RGB(0xFBFDEF)
    public inline val GreenBackground: Color get() = RGB(0xE5F9E7)
    public inline val TealBackground: Color get() = RGB(0xE1F7F7)
    public inline val BlueBackground: Color get() = RGB(0xDFF0FF)
    public inline val VioletBackground: Color get() = RGB(0xEAE7FF)
    public inline val PurpleBackground: Color get() = RGB(0xF6E7FF)
    public inline val PinkBackground: Color get() = RGB(0xFFE3FB)
    public inline val BrownBackground: Color get() = RGB(0xF1E2D3)

    public inline val RedHeaderColor: Color get() = RedTextColor.toHSL().darken(.05)
    public inline val OliveHeaderColor: Color get() = OliveTextColor.toHSL().darken(.05)
    public inline val GreenHeaderColor: Color get() = GreenTextColor.toHSL().darken(.05)
    public inline val YellowHeaderColor: Color get() = YellowTextColor.toHSL().darken(.05)
    public inline val BlueHeaderColor: Color get() = BlueTextColor.toHSL().darken(.05)
    public inline val TealHeaderColor: Color get() = TealTextColor.toHSL().darken(.05)
    public inline val PinkHeaderColor: Color get() = PinkTextColor.toHSL().darken(.05)
    public inline val VioletHeaderColor: Color get() = VioletTextColor.toHSL().darken(.05)
    public inline val PurpleHeaderColor: Color get() = PurpleTextColor.toHSL().darken(.05)
    public inline val OrangeHeaderColor: Color get() = OrangeTextColor.toHSL().darken(.05)
    public inline val BrownHeaderColor: Color get() = BrownTextColor.toHSL().darken(.05)

    public inline val RedTextColor: Color get() = Red
    public inline val OrangeTextColor: Color get() = Orange
    public inline val YellowTextColor: Color get() = RGB(0xB58105)
    public inline val OliveTextColor: Color get() = RGB(0x8ABC1E)
    public inline val GreenTextColor: Color get() = RGB(0x1EBC30)
    public inline val TealTextColor: Color get() = RGB(0x10A3A3)
    public inline val BlueTextColor: Color get() = Blue
    public inline val VioletTextColor: Color get() = Violet
    public inline val PurpleTextColor: Color get() = Purple
    public inline val PinkTextColor: Color get() = Pink
    public inline val BrownTextColor: Color get() = Brown

    public inline val RedBorderColor: Color get() = RedTextColor
    public inline val OrangeBorderColor: Color get() = OrangeTextColor
    public inline val YellowBorderColor: Color get() = YellowTextColor
    public inline val OliveBorderColor: Color get() = OliveTextColor
    public inline val GreenBorderColor: Color get() = GreenTextColor
    public inline val TealBorderColor: Color get() = TealTextColor
    public inline val BlueBorderColor: Color get() = BlueTextColor
    public inline val VioletBorderColor: Color get() = VioletTextColor
    public inline val PurpleBorderColor: Color get() = PurpleTextColor
    public inline val PinkBorderColor: Color get() = PinkTextColor
    public inline val BrownBorderColor: Color get() = BrownTextColor

    public inline val SubtleTransparentBlack: Color get() = RGB(0, 0, 0, 0.03)
    public inline val TransparentBlack: Color get() = RGB(0, 0, 0, 0.05)
    public inline val StrongTransparentBlack: Color get() = RGB(0, 0, 0, 0.10)
    public inline val VeryStrongTransparentBlack: Color get() = RGB(0, 0, 0, 0.15)

    public inline val SubtleTransparentWhite: Color get() = RGB(255, 255, 255, 0.02)
    public inline val TransparentWhite: Color get() = RGB(255, 255, 255, 0.08)
    public inline val StrongTransparentWhite: Color get() = RGB(255, 255, 255, 0.15)
}
