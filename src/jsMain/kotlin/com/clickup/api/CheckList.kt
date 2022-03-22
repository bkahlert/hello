@file:UseSerializers(DateAsMillisecondsSerializer::class, DurationAsMillisecondsSerializer::class, UrlSerializer::class)

package com.clickup.api

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.DurationAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class CheckList(
    @SerialName("id") val id: CheckListID,
    @SerialName("task_id") val taskId: TaskID,
    @SerialName("name") val name: String,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("orderindex") val orderIndex: Int?,
    @SerialName("creator") val creator: UserID,
    @SerialName("resolved") val resolvedCount: Int,
    @SerialName("unresolved") val unresolvedCount: Int,
    @SerialName("items") val items: List<CheckListItem>,
)

@Serializable value class CheckListID(override val id: String) : Identifier<String>

@Serializable
data class CheckListItem(
    @SerialName("id") val id: ID,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int?,
    @SerialName("assignee") val assignee: UserID?,
    @SerialName("resolved") val resolved: Boolean,
    @SerialName("parent") val parent: ID?,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("children") val children: List<CheckListItem>,
) {
    @Serializable value class ID(val id: String)
}
