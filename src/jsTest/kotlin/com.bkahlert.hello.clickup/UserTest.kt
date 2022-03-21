package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.SerializerTest
import com.clickup.api.User
import com.clickup.api.UserID
import io.ktor.http.Url

class UserTest : SerializerTest<User>(User.serializer(),
    // language=JSON
    """
        {
            "id": 42,
            "username": "john.doe",
            "email": "john.doe@example.com",
            "color": "#ff0000",
            "profilePicture": "https://example.com/john-doe.jpg",
            "initials": "JD",
            "week_start_day": 1,
            "global_font_support": false,
            "timezone": "Europe/Berlin"
        }
    """.trimIndent() to user())

fun user(
    id: UserID = UserID(42),
    username: String = "john.doe",
    email: String = "john.doe@example.com",
    color: Color? = Color("#ff0000"),
    profilePicture: Url = Url("https://example.com/john-doe.jpg"),
    initials: String = "JD",
    weekStartDay: Int? = 1,
    globalFontSupport: Boolean? = false,
    timezone: String? = "Europe/Berlin",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    initials,
    weekStartDay,
    globalFontSupport,
    timezone,
)

fun otherUser(
    id: UserID = UserID(53),
    username: String = "jane.doe",
    email: String = "jane.doe@example.com",
    color: Color? = Color("#00ff00"),
    profilePicture: Url = Url("https://example.com/jane-doe.jpg"),
    initials: String = "JD",
    weekStartDay: Int? = 0,
    globalFontSupport: Boolean? = true,
    timezone: String? = "Europe/London",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    initials,
    weekStartDay,
    globalFontSupport,
    timezone,
)
