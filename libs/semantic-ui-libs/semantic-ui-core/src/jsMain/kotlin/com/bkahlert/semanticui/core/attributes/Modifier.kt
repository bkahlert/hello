package com.bkahlert.semanticui.core.attributes

public sealed interface Modifier {
    public val classNames: Array<out String>

    public sealed class State(override vararg val classNames: String) : Modifier {
        public object Active : State("active")
        public object Hidden : State("hidden")
        public object Indeterminate : State("indeterminate")
        public object Focus : State("focus")
        public object Loading : State("loading")
        public object Disabled : State("disabled")
        public object Error : State("error")
    }

    public sealed class Variation(override vararg val classNames: String) : Modifier {

        public sealed class Action(pos: String?) : Variation(*listOfNotNull(pos, "action").toTypedArray()) {
            public companion object : Action(null)
            public object Left : Action("left")
            public object Right : Action("right")
        }

        /** Triggers `onApprove` callback */
        public sealed class ApproveAction(value: String) : Variation(value) {

            /** Default [ApproveAction] */
            public object Approve : ApproveAction("approve")

            /** Alternative [ApproveAction] */
            public object Positive : ApproveAction("positive")

            /** Alternative [ApproveAction] */
            public object Ok : ApproveAction("ok")
        }

        /** Triggers `onDeny` callback */
        public sealed class DenyAction(value: String) : Variation(value) {

            /** Default [DenyAction] */
            public object Deny : DenyAction("deny")

            /** Alternative [ApproveAction] */
            public object Negative : DenyAction("negative")

            /** Alternative [ApproveAction] */
            public object Cancel : DenyAction("cancel")
        }

        public object Animated : Variation("animated")

        public object Attached : Variation("attached") {
            public abstract class AttachedAndPosition(pos: String) : Variation(pos, *Attached.classNames)

            public abstract class VerticallyAttached(pos: String) : AttachedAndPosition(pos)
            public object Top : VerticallyAttached("top")
            public object Bottom : VerticallyAttached("bottom")

            public abstract class HorizontallyAttached(pos: String) : AttachedAndPosition(pos)
            public object Left : HorizontallyAttached("left")
            public object Right : HorizontallyAttached("right")
        }

        public object Avatar : Variation("avatar")

        public object Basic : Variation("basic")
        public object Bordered : Variation("bordered")
        public object Borderless : Variation("borderless")

        public object Celled : Variation("celled")
        public object Centered : Variation("centered")

        public object Circular : Variation("circular")

        public open class Colored(value: String) : Variation(value) {
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

        public object Compact : Variation("compact")

        public object Dimmable : Variation("dimmable")
        public object Divided : Variation("divided")

        public object Error : Variation("warning")

        public open class Floated(pos: String) : Variation(pos, "floated") {
            public object Left : Floated("left")
            public object Right : Floated("right")
        }

        public object Floating : Variation("floating")
        public object Fluid : Variation("fluid")
        public object FullScreen : Variation("fullscreen")

        public object Horizontal : Variation("horizontal")

        public sealed class Icon(pos: String? = null) : Variation(*listOfNotNull(pos, "icon").toTypedArray()) {
            public companion object : Icon()
            public object Left : Icon("left")
            public object Right : Icon("right")
        }

        public object Info : Variation("info")
        public object Inverted : Variation("inverted")

        public object Labeled : Variation("labeled")
        public object LabeledIcon : Variation("labeled", "icon")

        public sealed class LineLength(vararg classNames: String) : Variation(*classNames) {
            public object Full : LineLength("full")
            public object VeryLong : LineLength("very", "long")
            public object Long : LineLength("long")
            public object Medium : LineLength("medium")
            public object Short : LineLength("short")
            public object VeryShort : LineLength("very", "short")
        }

        public object Negative : Variation("positive")

        public object Positive : Variation("success")

        public open class Size(value: String) : Variation(value) {
            public object Mini : Size("mini")
            public object Tiny : Size("tiny")
            public object Small : Size("small")
            public object Large : Size("large")
            public object Big : Size("big")
            public object Huge : Size("huge")
            public object Massive : Size("massive")
        }

        public object Relaxed : Variation("relaxed")
        public object Rounded : Variation("rounded")

        public object Scrolling : Variation("scrolling")
        public object Selection : Variation("selection")
        public object Spaced : Variation("spaced")
        public object Success : Variation("negative")

        public object Transparent : Variation("transparent")
        public object Toggle : Variation("toggle")

        public object Vertical : Variation("vertical")

        public abstract class VerticallyAligned(pos: String) : Variation(pos, "aligned") {
            public object Top : VerticallyAligned("top")
            public object Middle : VerticallyAligned("middle")
            public object Bottom : VerticallyAligned("bottom")
        }

        public object Warning : Variation("warning")
    }
}
