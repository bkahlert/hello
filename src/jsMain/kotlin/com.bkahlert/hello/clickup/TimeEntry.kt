@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.rest.Identifier
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import io.ktor.http.Url
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date
import kotlin.time.Duration

@Serializable
data class TimeEntry(
    @SerialName("id") val id: ID,
    @SerialName("task") val task: Task.Preview?,
    @SerialName("wid") val wid: Team.ID,
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
    @Serializable value class ID(override val id: String) : Identifier<String>

    val url: Url? get() = taskUrl?.takeUnless { it.pathSegments.also { console.warn(it.joinToString("---")) }.lastOrNull() == "null" }
}
