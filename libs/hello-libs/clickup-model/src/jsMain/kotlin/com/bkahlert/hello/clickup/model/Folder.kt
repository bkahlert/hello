package com.bkahlert.hello.clickup.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Folder(
    @SerialName("id") val id: FolderID,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int,
    @SerialName("override_statuses") val overrideStatuses: Boolean,
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("task_count") val taskCount: String,
    @SerialName("archived") val archived: Boolean,
    @SerialName("statuses") val statuses: List<Status>,
    @SerialName("lists") val lists: List<TaskList>,
    @SerialName("permission_level") val permissionLevel: String,
) {
    public fun asPreview(): FolderPreview = FolderPreview(
        id = id,
        name = name,
        hidden = hidden,
        access = true,
    )
}

@Serializable public value class FolderID(override val id: String) : Identifier<String>

@Serializable
public data class FolderPreview(
    @SerialName("id") val id: FolderID,
    @SerialName("name") val name: String,
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("access") val access: Boolean,
)
