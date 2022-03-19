package com.bkahlert.hello.plugins

import com.bkahlert.Brand
import com.bkahlert.hello.plugins.Pomodoro.Type.Companion.duration
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.Color.RGB
import com.clickup.api.Tag
import com.clickup.api.TimeEntry
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * A Pomodoro session of the given [duration] related to the given [timeEntryID].
 */
data class Pomodoro(
    /** ID of the [TimeEntry] this session relates to. */
    val timeEntryID: TimeEntry.ID,
    /** Duration of this session. */
    val duration: Duration,
) {
    /**
     * Well-known [Pomodoro] types.
     */
    @Suppress("RemoveRedundantQualifierName")
    enum class Type(
        /** Amount of time a [Pomodoro] of this type takes. */
        val duration: Duration,
        /** Color used for tags encoding this type. */
        tagColor: Color,
    ) {
        /** The original [Pomodoro] type encompassing 25 minutes. */
        Classic(25.minutes, Type.TOMATO),

        /** The [Pomodoro] type preferred by many brain-workers with a duration of 50 minutes. */
        Pro(50.minutes, Type.TOMATO_SAUCE),

        /** *used for debugging purposes* */
        Debug(10.seconds, Brand.colors.cyan);

        /**
         * A [Tag] encoding this [Pomodoro.Type].
         */
        val tag: Tag = Tag("${Type.TAG_PREFIX}${name.lowercase()}", tagColor, tagColor, null)

        /**
         * Returns a [Tag] list containing all non-[Pomodoro] related tags of the
         * specified [tags] and a tag encoding this [Pomodoro.Type].
         */
        fun addTag(tags: List<Tag>): List<Tag> = buildList {
            tags.mapNotNullTo(this) { existingTag ->
                existingTag.takeIf { it.duration == null }
            }
            add(tag)
        }

        companion object {
            private val TOMATO = RGB(0xff6347)
            private val TOMATO_SAUCE = RGB(0xb21807)
            private const val TAG_PREFIX = "pomodoro-"

            /**
             * Extracts a [Pomodoro.duration] from `this` [Tag].
             *
             * Matches the following tag names:
             * - pomodoro-<NAME>
             * - pomodoro-<MINUTES>
             */
            val Tag.duration: Duration?
                get() = name
                    .takeIf { it.startsWith(TAG_PREFIX) }
                    ?.removePrefix(TAG_PREFIX)
                    ?.let { suffix ->
                        suffix.toIntOrNull()?.minutes ?: enumValues<Type>().firstOrNull { type -> type.name.equals(suffix, ignoreCase = true) }?.duration
                    }

            /**
             * The default type to use in cases
             * where a pre-selection should be made
             * or information can't be retrieved.
             */
            val Default: Type get() = Classic
        }
    }

    companion object {

        /**
         * Formats a [Pomodoro.duration] in the form `hh:mm`.
         */
        fun Duration.format(): String {
            val absoluteString = absoluteValue.toComponents { days, hours, minutes, seconds, _ ->
                listOfNotNull(days.takeUnless { it == 0L }, hours.takeUnless { it == 0 }, minutes, seconds)
                    .joinToString(":") { it.toString().padStart(2, '0') }
            }.removePrefix("0")
            return if (isNegative()) "-$absoluteString" else absoluteString
        }

        /**
         * Creates a based on an existing [TimeEntry]
         * by looking for a matching tag to determine the [Pomodoro.duration]
         * (default: duration of [Pomodoro.Type.Default]).
         */
        fun of(timeEntry: TimeEntry): Pomodoro = Pomodoro(
            timeEntryID = timeEntry.id,
            duration = timeEntry.tags.firstNotNullOfOrNull { it.duration } ?: Type.Default.duration
        )
    }
}
