package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.serialization.Named
import com.bkahlert.hello.clickup.serialization.SerializerTest
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.uri.Uri

@Suppress("unused")
class TeamTest : SerializerTest<Team>(
    Team.serializer(),
    // language=JSON
    """
        {
            "id": "180",
            "name": "work group",
            "color": "#0000ff",
            "avatar": "https://example.com/work.png",
            "members": [
                {
                    "user": {
                        "id": 11111,
                        "username": "john.doe",
                        "email": "john.doe@example.com",
                        "color": "#ff0000",
                        "profilePicture": "${ClickUpFixtures.User.profilePicture}",
                        "initials": "JD",
                        "week_start_day": 1,
                        "global_font_support": false,
                        "timezone": "Europe/Berlin"
                    }
                },
                {
                    "user": {
                        "id": 53,
                        "username": "jane.doe",
                        "email": "jane.doe@example.com",
                        "color": "#00ff00",
                        "profilePicture": "https://example.com/jane-doe.jpg",
                        "initials": "JD",
                        "week_start_day": 0,
                        "global_font_support": true,
                        "timezone": "Europe/London"
                    }
                }
            ]
        }
    """.trimIndent() to team(),
)

fun team(
    id: TeamID = TeamID("180"),
    name: String = "work group",
    color: Color = RGB("#0000ff"),
    avatar: Uri = Uri.parse("https://example.com/work.png"),
    vararg members: User = arrayOf(user(), otherUser()),
) = Team(
    id = id,
    name = name,
    color = color,
    avatar = avatar,
    members = members.map { Named.ofSingle(it) },
)
