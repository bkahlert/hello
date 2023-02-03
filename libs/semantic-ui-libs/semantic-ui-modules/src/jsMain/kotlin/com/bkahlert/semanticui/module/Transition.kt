package com.bkahlert.semanticui.module

import com.bkahlert.semanticui.core.jQuery
import kotlin.js.Json
import kotlin.js.json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

// TODO demo

/** Triggers a Semantic UI transition with the specified [options]. */
public fun jQuery.transition(options: Json): jQuery =
    asDynamic().transition(options).unsafeCast<jQuery>()

/** Triggers a Semantic UI transition [behavior] the specified [args]. */
// TODO add https://semantic-ui.com/modules/transition.html#behavior
public fun jQuery.transition(behavior: String, vararg args: Any?): jQuery =
    asDynamic().transition.apply(this, arrayOf(behavior, *args)).unsafeCast<jQuery>()

/** Triggers a Semantic UI [transition] with the optional [animation], [duration] and [onComplete]. */
public fun jQuery.transition(
    animation: String? = null,
    duration: Duration? = null,
    onComplete: (() -> Unit)? = null
): jQuery = asDynamic().transition(
    animation,
    duration?.inWholeMilliseconds?.toInt(),
    onComplete,
).unsafeCast<jQuery>()

/** Triggers the specific Semantic UI [transition]. */
public fun jQuery.transition(transition: Transition): jQuery =
    asDynamic().transition(transition).unsafeCast<jQuery>()

/**
 * Semantic UI [transition](https://semantic-ui.com/modules/transition.html)
 */
public data class Transition(
    /** Named animation event to use. Must be defined in CSS. */
    public val animation: String? = "fade",
    /** Interval between each elements transition. */
    public val interval: Duration? = Duration.ZERO,
    /** When an interval is specified, sets order of animations. `auto` reverses only animations that are hiding. */
    public val reverse: String? = "auto",
    /** Specify the final display type (block, inline-block, etc.) so that it doesn't have to be calculated. */
    public val displayType: Boolean? = false,
    /** Duration of the CSS transition animation. */
    public val duration: Duration? = 500.milliseconds,
    /** If enabled, a timeout is added to ensure animationend callback occurs even if the element is hidden. */
    public val useFailSafe: Boolean? = true,
    /** If enabled, allows the same animation to be queued while it's already occurring. */
    public val allowRepeats: Boolean? = false,
    /** Whether to automatically queue animation if another is occurring. */
    public val queue: Boolean? = true,

    /** Callback on each transition that changes visibility to `shown`. */
    public val onShow: (() -> Unit)? = null,
    /** Callback on each transition that changes visibility to `hidden`. */
    public val onHide: (() -> Unit)? = null,
    /** Callback on animation start, useful for queued animations. */
    public val onStart: (() -> Unit)? = null,
    /** Callback on each transition complete. */
    public val onComplete: (() -> Unit)? = null,
) : Json by json(
    "animation" to animation,
    "interval" to interval?.inWholeMilliseconds?.toInt(),
    "reverse" to reverse,
    "displayType" to displayType,
    "duration" to duration?.let { "${it.inWholeMilliseconds.toInt()}ms" },
    "useFailSafe" to useFailSafe,
    "allowsRepeats" to allowRepeats,
    "queue" to queue,
    "onShow" to onShow,
    "onHide" to onHide,
    "onStart" to onStart,
    "onComplete" to onComplete,
)
