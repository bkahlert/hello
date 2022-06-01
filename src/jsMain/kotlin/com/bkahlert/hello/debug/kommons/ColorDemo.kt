package com.bkahlert.hello.debug.kommons

import androidx.compose.runtime.Composable
import com.bkahlert.Brand
import com.bkahlert.hello.debug.Demo
import com.bkahlert.hello.debug.Demos
import com.bkahlert.hello.semanticui.SiteColors
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.text.splitPascalCase
import org.jetbrains.compose.web.css.AlignContent
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.alignContent
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.deg
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Small
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement

@Composable
fun SiteColorsDemo() {
    Demos("Site Colors") {

        mapOf(
            "Base Colors" to mapOf(
                SiteColors::Red to SiteColors.Red,
                SiteColors::Orange to SiteColors.Orange,
                SiteColors::Yellow to SiteColors.Yellow,
                SiteColors::Olive to SiteColors.Olive,
                SiteColors::Green to SiteColors.Green,
                SiteColors::Teal to SiteColors.Teal,
                SiteColors::Blue to SiteColors.Blue,
                SiteColors::Violet to SiteColors.Violet,
                SiteColors::Purple to SiteColors.Purple,
                SiteColors::Pink to SiteColors.Pink,
                SiteColors::Brown to SiteColors.Brown,
                SiteColors::Grey to SiteColors.Grey,
                SiteColors::Black to SiteColors.Black,
            ),
            "Light Colors" to mapOf(
                SiteColors::LightRed to SiteColors.LightRed,
                SiteColors::LightOrange to SiteColors.LightOrange,
                SiteColors::LightYellow to SiteColors.LightYellow,
                SiteColors::LightOlive to SiteColors.LightOlive,
                SiteColors::LightGreen to SiteColors.LightGreen,
                SiteColors::LightTeal to SiteColors.LightTeal,
                SiteColors::LightBlue to SiteColors.LightBlue,
                SiteColors::LightViolet to SiteColors.LightViolet,
                SiteColors::LightPurple to SiteColors.LightPurple,
                SiteColors::LightPink to SiteColors.LightPink,
                SiteColors::LightBrown to SiteColors.LightBrown,
                SiteColors::LightGrey to SiteColors.LightGrey,
                SiteColors::LightBlack to SiteColors.LightBlack,
            ),
            "Greyscale Colors" to mapOf(
                SiteColors::FullBlack to SiteColors.FullBlack,
                SiteColors::OffWhite to SiteColors.OffWhite,
                SiteColors::DarkWhite to SiteColors.DarkWhite,
                SiteColors::MidWhite to SiteColors.MidWhite,
                SiteColors::White to SiteColors.White,
            ),
            "Background Colors" to mapOf(
                SiteColors::RedBackground to SiteColors.RedBackground,
                SiteColors::OrangeBackground to SiteColors.OrangeBackground,
                SiteColors::YellowBackground to SiteColors.YellowBackground,
                SiteColors::OliveBackground to SiteColors.OliveBackground,
                SiteColors::GreenBackground to SiteColors.GreenBackground,
                SiteColors::TealBackground to SiteColors.TealBackground,
                SiteColors::BlueBackground to SiteColors.BlueBackground,
                SiteColors::VioletBackground to SiteColors.VioletBackground,
                SiteColors::PurpleBackground to SiteColors.PurpleBackground,
                SiteColors::PinkBackground to SiteColors.PinkBackground,
                SiteColors::BrownBackground to SiteColors.BrownBackground,
            ),
            "Header Colors" to mapOf(
                SiteColors::RedHeaderColor to SiteColors.RedHeaderColor,
                SiteColors::OliveHeaderColor to SiteColors.OliveHeaderColor,
                SiteColors::GreenHeaderColor to SiteColors.GreenHeaderColor,
                SiteColors::YellowHeaderColor to SiteColors.YellowHeaderColor,
                SiteColors::BlueHeaderColor to SiteColors.BlueHeaderColor,
                SiteColors::TealHeaderColor to SiteColors.TealHeaderColor,
                SiteColors::PinkHeaderColor to SiteColors.PinkHeaderColor,
                SiteColors::VioletHeaderColor to SiteColors.VioletHeaderColor,
                SiteColors::PurpleHeaderColor to SiteColors.PurpleHeaderColor,
                SiteColors::OrangeHeaderColor to SiteColors.OrangeHeaderColor,
                SiteColors::BrownHeaderColor to SiteColors.BrownHeaderColor,
            ),
            "Text Colors" to mapOf(
                SiteColors::RedTextColor to SiteColors.RedTextColor,
                SiteColors::OrangeTextColor to SiteColors.OrangeTextColor,
                SiteColors::YellowTextColor to SiteColors.YellowTextColor,
                SiteColors::OliveTextColor to SiteColors.OliveTextColor,
                SiteColors::GreenTextColor to SiteColors.GreenTextColor,
                SiteColors::TealTextColor to SiteColors.TealTextColor,
                SiteColors::BlueTextColor to SiteColors.BlueTextColor,
                SiteColors::VioletTextColor to SiteColors.VioletTextColor,
                SiteColors::PurpleTextColor to SiteColors.PurpleTextColor,
                SiteColors::PinkTextColor to SiteColors.PinkTextColor,
                SiteColors::BrownTextColor to SiteColors.BrownTextColor,
            ),
            "Border Colors" to mapOf(
                SiteColors::RedBorderColor to SiteColors.RedBorderColor,
                SiteColors::OrangeBorderColor to SiteColors.OrangeBorderColor,
                SiteColors::YellowBorderColor to SiteColors.YellowBorderColor,
                SiteColors::OliveBorderColor to SiteColors.OliveBorderColor,
                SiteColors::GreenBorderColor to SiteColors.GreenBorderColor,
                SiteColors::TealBorderColor to SiteColors.TealBorderColor,
                SiteColors::BlueBorderColor to SiteColors.BlueBorderColor,
                SiteColors::VioletBorderColor to SiteColors.VioletBorderColor,
                SiteColors::PurpleBorderColor to SiteColors.PurpleBorderColor,
                SiteColors::PinkBorderColor to SiteColors.PinkBorderColor,
                SiteColors::BrownBorderColor to SiteColors.BrownBorderColor,
            ),
            "Emphasize Colors" to mapOf(
                SiteColors::SubtleTransparentBlack to SiteColors.SubtleTransparentBlack,
                SiteColors::TransparentBlack to SiteColors.TransparentBlack,
                SiteColors::StrongTransparentBlack to SiteColors.StrongTransparentBlack,
                SiteColors::VeryStrongTransparentBlack to SiteColors.VeryStrongTransparentBlack,
                SiteColors::SubtleTransparentWhite to SiteColors.SubtleTransparentWhite,
                SiteColors::TransparentWhite to SiteColors.TransparentWhite,
                SiteColors::StrongTransparentWhite to SiteColors.StrongTransparentWhite,
            ),
        ).forEach { (category, colors) ->
            Demo(category) {
                Tiles {
                    colors.forEach { (prop, color) ->
                        ColoredTile(color, prop.name.splitPascalCase().joinToString(" ")) { style { width((100.0 / colors.size).percent) } }
                    }
                }
            }
        }
    }
}

@Composable
fun RandomColorsDemo() {
    Demos("Random Colors") {
        val generators = listOf(
            "Random Color" to { Color.random() },
            "Random within Range" to { Color.random(0.7..0.8) },
            "Random with Base Hue" to { Color.random(0.4) },
            "Random with Base Hue And Variance" to { Color.random(0.4, 0.3) },
            "Random with Base Degree" to { Color.random(180.deg) },
            "Random with Base Degree And Variance" to { Color.random(180.deg, 10.deg) },
        )

        generators.forEach { (name, generator) ->
            Demo(name) {
                Tiles {
                    repeat(10) {
                        ColoredTile(generator()) { style { width(10.0.percent) } }
                    }
                }
            }
        }
    }
}

@Composable
fun ModifiedColorDemo() {
    Demos("Scaled Colors") {
        val colors = listOf(
            Brand.colors.black,
            Brand.colors.red,
            Brand.colors.green,
            Brand.colors.yellow,
            Brand.colors.blue,
            Brand.colors.magenta,
            Brand.colors.cyan,
            Brand.colors.white,
            Brand.colors.primary,
            Brand.colors.secondary,
            Brand.colors.border,
        )

        Demo("Scaled Saturation") {
            Tiles {
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleSaturation(-0.50)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleSaturation(-0.25)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleSaturation(+0.25)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleSaturation(+0.50)) { style { width((100.0 / colors.size).percent) } }
                }
            }
        }

        Demo("Scaled Lightness") {
            Tiles {
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleLightness(-0.50)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleLightness(-0.25)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleLightness(+0.25)) { style { width((100.0 / colors.size).percent) } }
                }
                colors.forEach { color ->
                    ColoredTile(color.toHSL().scaleLightness(+0.50)) { style { width((100.0 / colors.size).percent) } }
                }
            }
        }
    }
}

@Composable
private fun Tiles(
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<HTMLDivElement>? = null,
) {
    Div({
        style {
            display(DisplayStyle.Flex)
            flexWrap(FlexWrap.Wrap)
            alignContent(AlignContent.Center)
            justifyContent(JustifyContent.Center)
        }
        attrs?.invoke(this)
    }, content)
}


@Composable
private fun ColoredTile(
    color: Color,
    name: String = color.toString(),
    attrs: AttrBuilderContext<HTMLDivElement>? = null,
) {
    Div({
        style {
            color(color.textColor)
            width(50.px)
            height(50.px)
            backgroundColor(color)
            fontSize(.7.em)
        }
        attrs?.invoke(this)
    }) { Small { Text(name) } }
}
