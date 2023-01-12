@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.hello.url.URL
import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.minus
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
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
    @SerialName("description") val description: String,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("source") val source: String?,
    @SerialName("task_url") val taskUrl: URL?,
) {
    val duration: Duration? get() = end?.let { it - start }
    val passed: Duration get() = Date() - start
    val ended: Boolean get() = end != null
    val url: URL? get() = taskUrl?.takeUnless { Url(it.toString()).pathSegments.lastOrNull() == "null" }
}

@Serializable
value class TimeEntryID(override val id: String) : Identifier<String>
