@file:UseSerializers(DateAsMillisecondsSerializer::class, UrlSerializer::class)

package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.DateAsMillisecondsSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.js.Date

@Serializable
data class CheckList(
    @SerialName("id") val id: Int,
    @SerialName("task_id") val taskId: String,
    @SerialName("name") val name: String,
    @SerialName("date_created") val dateCreated: Date?,
    @SerialName("orderindex") val orderIndex: Int?,
    @SerialName("creator") val creator: Int,
    @SerialName("resolved") val resolved: Int,
    @SerialName("unresolved") val unresolved: Int,
    @SerialName("items") val items: List<Item>,
) {
    @Serializable
    data class Item(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
        @SerialName("orderindex") val orderIndex: Int?,
        @SerialName("assignee") val assignee: String?,
        @SerialName("resolved") val resolved: Boolean,
        @SerialName("parent") val parent: String?,
        @SerialName("dateCreated") val dateCreated: Date?,
        @SerialName("children") val children: List<String>,
    )
}
