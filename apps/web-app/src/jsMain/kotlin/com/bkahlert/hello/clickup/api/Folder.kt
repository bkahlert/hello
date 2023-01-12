@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup.api

import com.bkahlert.hello.url.UrlSerializer
import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class Folder(
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
    fun asPreview() = FolderPreview(
        id = id,
        name = name,
        hidden = hidden,
        access = true,
    )
}

@Serializable value class FolderID(override val id: String) : Identifier<String>

@Serializable
data class FolderPreview(
    @SerialName("id") val id: FolderID,
    @SerialName("name") val name: String,
    @SerialName("hidden") val hidden: Boolean,
    @SerialName("access") val access: Boolean,
)
