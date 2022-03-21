package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.SerializerTest
import com.clickup.api.Assignee
import com.clickup.api.FolderID
import com.clickup.api.FolderPreview
import com.clickup.api.SpaceID
import com.clickup.api.SpacePreview
import com.clickup.api.TaskList
import com.clickup.api.TaskListID
import com.clickup.api.TaskListPriority
import com.clickup.api.TaskListStatus
import io.ktor.http.Url
import kotlin.js.Date

class ClickupListTest : SerializerTest<TaskList>(TaskList.serializer(),
    // language=JSON
    """
        {
            "id": "25510969",
            "name": "Professional",
            "orderindex": 0,
            "status": {
                "status": "professional",
                "color": "#02bcd4"
            },
            "assignee": {
                "color": "#4169e1",
                "username": "Björn Kahlert",
                "initials": "BK",
                "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
            },
            "task_count": 45,
            "folder": {
                "id": "11087491",
                "name": "hidden",
                "hidden": true,
                "access": true
            },
            "space": {
                "id": "4564985",
                "name": "Personal",
                "access": true
            },
            "archived": false,
            "override_statuses": false,
            "permission_level": "create"
        }
    """.trimIndent() to clickupList())


fun clickupList(
    id: TaskListID = TaskListID("25510969"),
    name: String = "Professional",
    orderIndex: Int = 0,
    content: String? = null,
    status: TaskListStatus = TaskListStatus("professional", Color("#02BCD4")),
    priority: TaskListPriority? = null,
    assignee: Assignee = Assignee(
        color = Color("#4169E1"),
        username = "Björn Kahlert",
        initials = "BK",
        profilePicture = Url("https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"),
    ),
    taskCount: Int = 45,
    dueDate: Date? = null,
    startDate: Date? = null,
    folder: FolderPreview = FolderPreview(
        id = FolderID("11087491"),
        name = "hidden",
        hidden = true,
        access = true
    ),
    space: SpacePreview = SpacePreview(
        SpaceID("4564985"),
        "Personal",
        true
    ),
    archived: Boolean = false,
    overrideStatuses: Boolean = false,
    permissionLevel: String = "create",
) = TaskList(
    id,
    name,
    orderIndex,
    content,
    status,
    priority,
    assignee,
    taskCount,
    dueDate,
    startDate,
    folder,
    space,
    archived,
    overrideStatuses,
    permissionLevel,
)
