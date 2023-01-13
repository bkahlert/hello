package com.bkahlert.hello.semanticui.attributes

public open class Variation(override vararg val classNames: String) : Modifier {
    public object Avatar : Variation("avatar")
    public object Inline : Variation("inline")
    public object Fitted : Variation("fitted")
    public object Compact : Variation("compact")
    public object Positive : Variation("positive")
    public object Negative : Variation("negative")
    public object FullScreen : Variation("fullscreen")
    public object Longer : Variation("longer")
    public object Size {
        public val Mini: Variation = Variation("mini")
        public val Tiny: Variation = Variation("tiny")
        public val Small: Variation = Variation("small")
        public val Large: Variation = Variation("large")
        public val Big: Variation = Variation("big")
        public val Huge: Variation = Variation("huge")
        public val Massive: Variation = Variation("massive")
    }

    public object Colored {
        public val Red: Variation = Variation("red")
        public val Orange: Variation = Variation("orange")
        public val Yellow: Variation = Variation("yellow")
        public val Olive: Variation = Variation("olive")
        public val Green: Variation = Variation("green")
        public val Teal: Variation = Variation("teal")
        public val Blue: Variation = Variation("blue")
        public val Violet: Variation = Variation("violet")
        public val Purple: Variation = Variation("purple")
        public val Pink: Variation = Variation("pink")
        public val Brown: Variation = Variation("brown")
        public val Grey: Variation = Variation("grey")
        public val Black: Variation = Variation("black")
    }

    public object Flipped {
        public val Horizontally: Variation = Variation("horizontally")
        public val Vertically: Variation = Variation("vertically")
    }

    public object Rotated {
        public val Clockwise: Variation = Variation("clockwise")
        public val Counterclockwise: Variation = Variation("counterclockwise")
    }

    public object Link : Variation("link")
    public object Circular : Variation("circular")
    public object Bordered : Variation("bordered")
    public object Inverted : Variation("inverted")
    public object Horizontal : Variation("horizontal")
    public object Selection : Variation("selection")
    public object Animated : Variation("animated")
    public object Relaxed : Variation("relaxed")
    public object Divided : Variation("divided")
    public object Celled : Variation("celled")

    public object Corner : Variation("corner")

    public object Position {
        public val Top: Variation = Variation("top")
        public val Right: Variation = Variation("right")
        public val Bottom: Variation = Variation("bottom")
        public val Left: Variation = Variation("left")
    }

    public object Direction {
        public val Top: Variation = Variation("top")
        public val Right: Variation = Variation("right")
        public val Bottom: Variation = Variation("bottom")
        public val Left: Variation = Variation("left")
    }

    public object Columns {
        public val One: Variation = Variation("one")
        public val Two: Variation = Variation("two")
        public val Three: Variation = Variation("three")
        public val Four: Variation = Variation("four")
    }

    public object Aligned : Variation("aligned") {
        public val Left: Variation = Variation("left", *classNames)
        public val Center: Variation = Variation("center", *classNames)
        public val Right: Variation = Variation("right", *classNames)
        public val Justified: Variation = Variation("justified")
    }

    public object VerticallyAligned : Variation("aligned") {
        public val Top: Variation = Variation("top", *classNames)
        public val Middle: Variation = Variation("middle", *classNames)
        public val Bottom: Variation = Variation("bottom", *classNames)
    }

    public object Floated : Variation("floated") {
        public val Right: Variation = Variation("right", *classNames)
    }

    public object Centered : Variation("centered")
    public object Spaced : Variation("spaced")
    public object Fluid : Variation("fluid")
    public object Rounded : Variation("rounded")

    public object Floating : Variation("floating")
    public object Borderless : Variation("borderless")
    public object Labeled : Variation("labeled")
    public object Action : Variation("action")
    public class Icon(vararg names: String) : Variation(*names, "icon") {
        public companion object : Variation("icon")
    }

    public object Transparent : Variation("transparent")

    public object Scrolling : Variation("scrolling")
    public object Attached : Variation("attached") {
        public val Top: Variation = Variation("top", *classNames)
        public val Right: Variation = Variation("right", *classNames)
        public val Bottom: Variation = Variation("bottom", *classNames)
        public val Left: Variation = Variation("left", *classNames)
    }

    public object Padded : Variation("padded")

    public object Basic : Variation("basic")

    public object Emphasis {
        public val Primary: Variation = Variation("primary")
        public val Secondary: Variation = Variation("secondary")
        public val Tertiary: Variation = Variation("tertiary")
    }

    public object Clearing : Variation("clearing")
    public object Fullscreen : Variation("fullscreen")

    public object Length : Variation() {
        public val Full: Variation = Variation("full")
        public val VeryLong: Variation = Variation("very", "long")
        public val Long: Variation = Variation("long")
        public val Medium: Variation = Variation("medium")
        public val Short: Variation = Variation("short")
        public val VeryShort: Variation = Variation("very", "short")
    }

    public object Long : Variation("long")
    public object Actions : Variation() {
        public val Approve: Variation = Variation("approve")
        public val Positive: Variation = Variation("positive")
        public val Ok: Variation = Variation("ok")
        public val Deny: Variation = Variation("deny")
        public val Negative: Variation = Variation("Negative")
        public val Cancel: Variation = Variation("Cancel")
    }

    public object Dimmable : Variation("dimmable")
}
