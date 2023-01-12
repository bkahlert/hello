package com.bkahlert.hello.clickup.client

import com.bkahlert.hello.clickup.model.Tag
import com.bkahlert.hello.clickup.model.TaskID
import kotlinx.serialization.Serializable

@Serializable
public data class StartTimeEntryRequest(
    val tid: TaskID?,
    val description: String?,
    val billable: Boolean,
    val tags: List<Tag>,
)
