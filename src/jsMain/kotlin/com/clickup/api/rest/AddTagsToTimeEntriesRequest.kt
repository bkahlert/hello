@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api.rest

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import com.clickup.api.Tag
import com.clickup.api.TimeEntryID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class AddTagsToTimeEntriesRequest(
    @SerialName("time_entry_ids") val timeEntryIDs: List<TimeEntryID>,
    @SerialName("tags") val tags: List<Tag>,
)
