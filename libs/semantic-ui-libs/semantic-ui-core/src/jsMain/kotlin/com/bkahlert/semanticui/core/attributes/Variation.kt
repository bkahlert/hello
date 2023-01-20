package com.bkahlert.semanticui.core.attributes


public open class Variation(
    override vararg val classNames: String,
) : Modifier {
    public object Avatar : Variation("avatar")
    public object Inline : Variation("inline")
    public object Fitted : Variation("fitted")
    public object Compact : Variation("compact")

    public object FullScreen : Variation("fullscreen")
    public object Longer : Variation("longer")

    public open class Size(vararg classNames: String) : Variation(*classNames) {
        public object Mini : Size("mini")
        public object Tiny : Size("tiny")
        public object Small : Size("small")
        public object Large : Size("large")
        public object Big : Size("big")
        public object Huge : Size("huge")
        public object Massive : Size("massive")
    }

    public open class Colored(vararg classNames: String) : Variation(*classNames) {
        public object Red : Colored("red")
        public object Orange : Colored("orange")
        public object Yellow : Colored("yellow")
        public object Olive : Colored("olive")
        public object Green : Colored("green")
        public object Teal : Colored("teal")
        public object Blue : Colored("blue")
        public object Violet : Colored("violet")
        public object Purple : Colored("purple")
        public object Pink : Colored("pink")
        public object Brown : Colored("brown")
        public object Grey : Colored("grey")
        public object Black : Colored("black")
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
        public class AttachedAndPosition(vararg classNames: String) : Variation(*classNames, *Attached.classNames)

        public val Top: AttachedAndPosition = AttachedAndPosition("top")
        public val Right: AttachedAndPosition = AttachedAndPosition("right")
        public val Bottom: AttachedAndPosition = AttachedAndPosition("bottom")
        public val Left: AttachedAndPosition = AttachedAndPosition("left")
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
    public open class Actions(vararg classNames: String) : Variation(*classNames) {
        public object Approve : Actions("approve")
        public object Positive : Actions("positive")
        public object Ok : Actions("ok")
        public object Deny : Actions("deny")
        public object Negative : Actions("Negative")
        public object Cancel : Actions("Cancel")
    }

    public object Warning : Variation("warning")
    public object Info : Variation("info")
    public object Positive : Variation("positive")
    public object Success : Variation("success")
    public object Negative : Variation("negative")
    public object Error : Variation("error")

    public object Dimmable : Variation("dimmable")
}
