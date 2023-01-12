package com.bkahlert.hello.clickup.client

import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TimeEntryID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AddTagsToTimeEntriesRequest(
    @SerialName("time_entry_ids") val timeEntryIDs: List<TimeEntryID>,
    @SerialName("tags") val tags: List<Tag>,
)
