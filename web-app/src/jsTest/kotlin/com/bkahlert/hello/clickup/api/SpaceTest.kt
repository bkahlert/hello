package com.bkahlert.hello.clickup.api

import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.serialization.SerializerTest

@Suppress("unused")
class SpaceTest : SerializerTest<Space>(
    Space.serializer(),
    // language=JSON
    """
        {
            "id": "1",
            "name": "Personal",
            "private": false,
            "statuses": [
                {
                    "id": "p1_todo",
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                {
                    "id": "p1_inprogress",
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                {
                    "id": "p1_closed",
                    "status": "Closed",
                    "color": "#6bc950",
                    "orderindex": 2,
                    "type": "closed"
                }
            ],
            "multiple_assignees": false
        }
    """.trimIndent() to space()
)

fun space(
    id: SpaceID = SpaceID("1"),
    name: String = "Personal",
    private: Boolean = false,
    statuses: List<Status> = listOf(
        Status(
            StatusID("p1_todo"),
            "to do",
            Color(0x02BCD4),
            0,
            "open",
        ),
        Status(
            StatusID("p1_inprogress"),
            "in progress",
            Color(0xa875ff),
            1,
            "custom",
        ),
        Status(
            StatusID("p1_closed"),
            "Closed",
            Color(0x6bc950),
            2,
            "closed",
        )
    ),
    multipleAssignees: Boolean = false,
) = Space(
    id,
    name,
    private,
    statuses,
    multipleAssignees,
)
