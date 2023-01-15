package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.color.Color
import com.bkahlert.kommons.json.Lenient
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SpaceTest {

    @Test
    fun serialize() {
        Json.Lenient.encodeToString(space()) shouldEqualJson spaceJson
    }

    @Test
    fun deserialize() {
        Json.Lenient.decodeFromString<Space>(spaceJson) shouldBe space()
    }
}

// language=JSON
val spaceJson = """
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
""".trimIndent()

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
