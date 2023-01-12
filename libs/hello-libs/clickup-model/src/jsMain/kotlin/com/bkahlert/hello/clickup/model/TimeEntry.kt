package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.serialization.DateAsMilliseconds
import com.bkahlert.hello.url.URL
import com.bkahlert.kommons.minus
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date
import kotlin.time.Duration

@Serializable
public data class TimeEntry(
    @SerialName("id") val id: TimeEntryID,
    @SerialName("task") val task: TaskPreview?,
    @SerialName("wid") val wid: TeamID,
    @SerialName("user") val user: User,
    @SerialName("billable") val billable: Boolean,
    @SerialName("start") val start: DateAsMilliseconds,
    @SerialName("end") val end: DateAsMilliseconds?,
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
public value class TimeEntryID(override val id: String) : Identifier<String>
