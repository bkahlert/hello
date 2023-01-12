package com.bkahlert.hello.clickup.api.rest

import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TimeEntryID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddTagsToTimeEntriesRequest(
    @SerialName("time_entry_ids") val timeEntryIDs: List<TimeEntryID>,
    @SerialName("tags") val tags: List<Tag>,
)
