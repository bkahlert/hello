package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.Pomodoro.Type.Companion.duration
import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TimeEntry
import com.bkahlert.hello.clickup.model.TimeEntryID
import com.bkahlert.hello.clickup.serialization.DurationAsMilliseconds
import com.bkahlert.kommons.Creator.Companion.creator
import com.bkahlert.kommons.Creator.Creator1
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.color.Colors
import com.bkahlert.kommons.minus
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * A Pomodoro session of the given [duration] related to the given [timeEntryID].
 */
@Serializable
public data class Pomodoro(
    /** ID of the [TimeEntry] this session relates to. */
    val timeEntryID: TimeEntryID,
    /** Duration of this session. */
    val duration: DurationAsMilliseconds,
    val status: Status,
) {
    /**
     * Well-known [Pomodoro] types.
     */
    @Suppress("RemoveRedundantQualifierName")
    @Serializable
    public enum class Type(
        /** Amount of time a [Pomodoro] of this type takes. */
        public val duration: DurationAsMilliseconds,
        /** Color used for tags encoding this type. */
        tagColor: Color,
    ) {
        /** The original [Pomodoro] type encompassing 25 minutes. */
        Classic(25.minutes, Color.Tomato),

        /** The [Pomodoro] type preferred by many brain-workers with a duration of 50 minutes. */
        Pro(50.minutes, Color.TomatoSauce),

        /** *used for debugging purposes* */
        Debug(10.seconds, Colors.cyan);

        /**
         * A [Tag] encoding this [Pomodoro.Type].
         */
        public val tag: Tag = Tag("$TAG_PREFIX${name.lowercase()}", tagColor, tagColor, null)

        /**
         * Returns a [Tag] list containing all non-[Pomodoro] related tags of the
         * specified [tags] and a tag encoding this [Pomodoro.Type].
         */
        public fun addTag(tags: List<Tag>): List<Tag> = buildList {
            tags.mapNotNullTo(this) { existingTag ->
                existingTag.takeIf { it.duration == null }
            }
            add(tag)
        }

        /**
         * Extracts a [Pomodoro.Type] from `this` [Tag].
         *
         * Matches the following tag names:
         * - pomodoro-<NAME>
         * - pomodoro-<MINUTES>
         */
        public companion object : Creator1<Tag, Type> by (creator({ tag ->
            tag.suffix?.let { suffix ->
                when (val minutes = suffix.toLongOrNull()) {
                    null -> enumValues<Type>().firstOrNull { type -> type.name.equals(suffix, ignoreCase = true) }
                    else -> enumValues<Type>().firstOrNull { type -> type.duration.inWholeMinutes == minutes }
                }
            }
        })) {

            /**
             * Extracts a [Pomodoro.duration] from `this` [Tag].
             *
             * Matches the following tag names:
             * - pomodoro-<NAME>
             * - pomodoro-<MINUTES>
             */
            public val Tag.duration: Duration?
                get() = suffix?.let { suffix ->
                    suffix.toIntOrNull()?.minutes ?: enumValues<Type>().firstOrNull { type -> type.name.equals(suffix, ignoreCase = true) }?.duration
                }

            /**
             * The default type to use in cases
             * where a pre-selection should be made
             * or information can't be retrieved.
             */
            public val Default: Type get() = Classic
        }
    }

    public enum class Status(public val color: Color) {
        Prepared(RGB(0x0e566c)), Running(RGB(0x794b02)), Aborted(RGB(0x912d2b)), Completed(RGB(0x1a531b));

        public val tag: Tag = Tag("$TAG_PREFIX${name.lowercase()}", color, color)

        public companion object {
            public fun of(tag: Tag): Status? = tag.suffix?.let { suffix -> values().firstOrNull { it.name.equals(suffix, ignoreCase = true) } }
            public fun of(tags: List<Tag>): Status? = tags.firstNotNullOfOrNull { of(it) }
            public fun of(timeEntry: TimeEntry): Status = of(timeEntry.tags) ?: timeEntry.run {
                @Suppress("SENSELESS_COMPARISON")
                if (timeEntry.start == null) Prepared
                else if (timeEntry.end == null) Running
                else {
                    val pomodoroDuration = tags.firstNotNullOfOrNull { it.duration } ?: Type.Default.duration
                    if (timeEntry.end - timeEntry.start < pomodoroDuration) Aborted
                    else Completed
                }
            }
        }
    }

    /**
     * Creates a based on an existing [TimeEntry]
     * by looking for a matching tag to determine the [Pomodoro.duration]
     * (default: duration of [Pomodoro.Type.Default]).
     */
    public companion object : Creator1<TimeEntry, Pomodoro> by (creator({ timeEntry: TimeEntry ->
        Pomodoro(
            timeEntryID = timeEntry.id,
            duration = timeEntry.tags.firstNotNullOfOrNull { it.duration } ?: Type.Default.duration,
            status = Status.of(timeEntry)
        )
    })) {
        private const val TAG_PREFIX = "pomodoro-"
        private val Tag.suffix: String? get() = name.takeIf { it.startsWith(TAG_PREFIX) }?.removePrefix(TAG_PREFIX)

        /**
         * Formats a [Pomodoro.duration] in the form `hh:mm`.
         */
        public fun Duration.format(): String {
            val absoluteString = absoluteValue.toComponents { days, hours, minutes, seconds, _ ->
                listOfNotNull(days.takeUnless { it == 0L }, hours.takeUnless { it == 0 }, minutes, seconds)
                    .joinToString(":") { it.toString().padStart(2, '0') }
            }.removePrefix("0")
            return if (isNegative()) "-$absoluteString" else absoluteString
        }
    }
}
