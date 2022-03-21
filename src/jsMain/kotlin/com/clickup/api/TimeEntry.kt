@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.rest.Identifier
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date
import kotlin.time.Duration

@Serializable
data class TimeEntry(
    @SerialName("id") val id: TimeEntryID,
    @SerialName("task") val task: TaskPreview?,
    @SerialName("wid") val wid: TeamID,
    @SerialName("user") val user: User,
    @SerialName("billable") val billable: Boolean,
    @SerialName("start") val start: Date,
    @SerialName("end") val end: Date?,
    @SerialName("duration") val duration: Duration,
    @SerialName("description") val description: String,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("source") val source: String?,
    @SerialName("at") val at: Date,
    @SerialName("task_url") val taskUrl: Url?,
) {

    val url: Url? get() = taskUrl?.takeUnless { it.pathSegments.also { console.warn(it.joinToString("---")) }.lastOrNull() == "null" }
}

@Serializable value class TimeEntryID(override val id: String) : Identifier<String>
