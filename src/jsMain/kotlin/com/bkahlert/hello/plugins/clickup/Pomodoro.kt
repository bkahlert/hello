@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.plugins.clickup

import com.bkahlert.Brand
import com.bkahlert.hello.plugins.clickup.Pomodoro.Type.Companion.duration
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.bkahlert.kommons.time.minus
import com.clickup.api.Tag
import com.clickup.api.TimeEntry
import com.clickup.api.TimeEntryID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * A Pomodoro session of the given [duration] related to the given [timeEntryID].
 */
@Serializable
data class Pomodoro(
    /** ID of the [TimeEntry] this session relates to. */
    val timeEntryID: TimeEntryID,
    /** Duration of this session. */
    val duration: Duration,
    val status: Status,
) {
    /**
     * Well-known [Pomodoro] types.
     */
    @Suppress("RemoveRedundantQualifierName")
    @Serializable
    enum class Type(
        /** Amount of time a [Pomodoro] of this type takes. */
        val duration: Duration,
        /** Color used for tags encoding this type. */
        tagColor: Color,
    ) {
        /** The original [Pomodoro] type encompassing 25 minutes. */
        Classic(25.minutes, Color.Tomato),

        /** The [Pomodoro] type preferred by many brain-workers with a duration of 50 minutes. */
        Pro(50.minutes, Color.TomatoSauce),

        /** *used for debugging purposes* */
        Debug(10.seconds, Brand.colors.cyan);

        /**
         * A [Tag] encoding this [Pomodoro.Type].
         */
        val tag: Tag = Tag("$TAG_PREFIX${name.lowercase()}", tagColor, tagColor, null)

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

            /**
             * Extracts a [Pomodoro.Type] from `this` [Tag].
             *
             * Matches the following tag names:
             * - pomodoro-<NAME>
             * - pomodoro-<MINUTES>
             */
            fun of(tag: Tag): Pomodoro.Type? = tag.suffix?.let { suffix ->
                when (val minutes = suffix.toLongOrNull()) {
                    null -> enumValues<Type>().firstOrNull { type -> type.name.equals(suffix, ignoreCase = true) }
                    else -> enumValues<Type>().firstOrNull { type -> type.duration.inWholeMinutes == minutes }
                }
            }

            /**
             * Extracts a [Pomodoro.duration] from `this` [Tag].
             *
             * Matches the following tag names:
             * - pomodoro-<NAME>
             * - pomodoro-<MINUTES>
             */
            val Tag.duration: Duration?
                get() = suffix?.let { suffix ->
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

    enum class Status(val color: Color) {
        Prepared(RGB(0x0e566c)), Running(RGB(0x794b02)), Aborted(RGB(0x912d2b)), Completed(RGB(0x1a531b));

        val tag: Tag = Tag("$TAG_PREFIX${name.lowercase()}", color, color)

        companion object {
            fun of(tag: Tag): Status? = tag.suffix?.let { suffix -> values().firstOrNull { it.name.equals(suffix, ignoreCase = true) } }
            fun of(tags: List<Tag>) = tags.firstNotNullOfOrNull { of(it) }
            fun of(timeEntry: TimeEntry) = of(timeEntry.tags) ?: timeEntry.run {
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

    companion object {
        private const val TAG_PREFIX = "pomodoro-"
        private val Tag.suffix: String? get() = name.takeIf { it.startsWith(TAG_PREFIX) }?.removePrefix(TAG_PREFIX)

        /**
         * Formats a [Pomodoro.duration] in the form `hh:mm`.
         */
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
            duration = timeEntry.tags.firstNotNullOfOrNull { it.duration } ?: Type.Default.duration,
            status = Status.of(timeEntry)
        )
    }
}
