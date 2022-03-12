package com.bkahlert.hello.clickup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Folder(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("override_statuses") val overrideStatuses: Boolean,
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("task_count") val taskCount: String,
    @SerialName("archived") val archived: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("lists") val lists: List<String>,
    @SerialName("permission_level") val permissionLevel: String,
) {

    @Serializable
    data class Preview(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String,
        @SerialName("hidden") val hidden: Boolean,
        @SerialName("access") val access: Boolean,
    )
}
