package com.bkahlert.hello.clickup

import com.bkahlert.hello.clickup.ClickupList.Priority
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.SerializerTest
import io.ktor.http.Url
import kotlin.js.Date

class ClickupListTest : SerializerTest<ClickupList>(ClickupList.serializer(),
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
    id: ClickupList.ID = ClickupList.ID("25510969"),
    name: String = "Professional",
    orderIndex: Int = 0,
    content: String? = null,
    status: ClickupList.Status = ClickupList.Status("professional", Color("#02BCD4")),
    priority: Priority? = null,
    assignee: Assignee = Assignee(
        color = Color("#4169E1"),
        username = "Björn Kahlert",
        initials = "BK",
        profilePicture = Url("https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"),
    ),
    taskCount: Int = 45,
    dueDate: Date? = null,
    startDate: Date? = null,
    folder: Folder.Preview = Folder.Preview(
        id = Folder.ID("11087491"),
        name = "hidden",
        hidden = true,
        access = true
    ),
    space: Space.Preview = Space.Preview(
        Space.ID("4564985"),
        "Personal",
        true
    ),
    archived: Boolean = false,
    overrideStatuses: Boolean = false,
    permissionLevel: String = "create",
) = ClickupList(
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
