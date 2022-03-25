@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api.rest

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.Tag
import com.clickup.api.TaskID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class StartTimeEntryRequest(
    val tid: TaskID?,
    val description: String?,
    val billable: Boolean,
    val tags: List<Tag>,
)
