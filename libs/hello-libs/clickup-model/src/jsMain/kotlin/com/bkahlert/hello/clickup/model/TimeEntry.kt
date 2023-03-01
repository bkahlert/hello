package com.bkahlert.hello.clickup.model

import com.bkahlert.kommons.time.InstantAsEpochMilliseconds
import com.bkahlert.kommons.time.InstantAsEpochMillisecondsSerializer
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUrl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
public data class TimeEntry(
    @SerialName("id") val id: TimeEntryID,
    @SerialName("task") val task: TaskPreview?,
    @SerialName("wid") val wid: TeamID,
    @SerialName("user") val user: User,
    @SerialName("billable") val billable: Boolean,
    @SerialName("start") @Serializable(InstantAsEpochMillisecondsSerializer::class) val start: InstantAsEpochMilliseconds,
    @SerialName("end") @Serializable(InstantAsEpochMillisecondsSerializer::class) val end: InstantAsEpochMilliseconds?,
    @SerialName("description") val description: String,
    @SerialName("tags") val tags: List<Tag>,
    @SerialName("source") val source: String?,
    @SerialName("task_url") val taskUrl: Uri?,
) {
    val duration: Duration? get() = end?.let { it - start }
    val passed: Duration get() = Now - start
    val ended: Boolean get() = end != null
    val url: Uri? get() = taskUrl?.takeUnless { it.toUrl().pathSegments.lastOrNull() == "null" }
}

@Serializable
public value class TimeEntryID(override val id: String) : Identifier<String>
