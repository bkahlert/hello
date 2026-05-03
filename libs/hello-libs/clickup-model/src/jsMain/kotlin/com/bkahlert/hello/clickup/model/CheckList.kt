package com.bkahlert.hello.clickup.model

import com.bkahlert.kommons.time.InstantAsEpochMilliseconds
import com.bkahlert.kommons.time.InstantAsEpochMillisecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CheckList(
    @SerialName("id") val id: CheckListID,
    @SerialName("task_id") val taskId: TaskID,
    @SerialName("name") val name: String,
    @SerialName("date_created") @Serializable(InstantAsEpochMillisecondsSerializer::class) val dateCreated: InstantAsEpochMilliseconds?,
    @SerialName("orderindex") val orderIndex: Int?,
    @SerialName("creator") val creator: UserID,
    @SerialName("resolved") val resolvedCount: Int,
    @SerialName("unresolved") val unresolvedCount: Int,
    @SerialName("items") val items: List<CheckListItem>,
)

@Serializable public value class CheckListID(override val id: String) : Identifier<String>

@Serializable
public data class CheckListItem(
    @SerialName("id") val id: ID,
    @SerialName("name") val name: String,
    @SerialName("orderindex") val orderIndex: Int?,
    @SerialName("assignee") val assignee: UserID?,
    @SerialName("resolved") val resolved: Boolean,
    @SerialName("parent") val parent: ID?,
    @SerialName("date_created") @Serializable(InstantAsEpochMillisecondsSerializer::class) val dateCreated: InstantAsEpochMilliseconds?,
    @SerialName("children") val children: List<CheckListItem>,
) {
    @Serializable public value class ID(public val id: String)
}
