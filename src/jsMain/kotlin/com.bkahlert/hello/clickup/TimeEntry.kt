@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date
import kotlin.time.Duration

@Serializable
data class TimeEntry(
    @SerialName("id") val id: String,
    @SerialName("task") val task: Task.Preview,
    @SerialName("wid") val wid: String,
    @SerialName("user") val user: User,
    @SerialName("billable") val billable: Boolean,
    @SerialName("start") val start: Date,
    @SerialName("end") val end: Date?,
    @SerialName("duration") val duration: Duration,
    @SerialName("description") val description: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("source") val source: String,
    @SerialName("at") val at: Date,
)
