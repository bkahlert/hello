package com.bkahlert.hello.clickup

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.Color
import io.kotest.matchers.shouldBe
import io.ktor.http.Url
import kotlin.test.Test

class UserTest {
    val json =
        // language=JSON
        """
            {
                "id": 42,
                "username": "john.doe",
                "email": "john.doe@example.com",
                "color": "#ff0000",
                "profilePicture": "https://example.com/john-doe.jpg",
                "week_start_day": 1,
                "global_font_support": false,
                "timezone": "Europe/Berlin"
            }
        """.trimIndent()

    @Test fun deserialize() {
        json.deserialize<User>() shouldBe user()
    }

    @Test fun serialize() {
        user().serialize() shouldBe json
    }
}

fun user(
    id: Int = 42,
    username: String = "john.doe",
    email: String = "john.doe@example.com",
    color: Color? = Color("#ff0000"),
    profilePicture: Url? = Url("https://example.com/john-doe.jpg"),
    weekStartDay: Int? = 1,
    globalFontSupport: Boolean? = false,
    timezone: String? = "Europe/Berlin",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    weekStartDay,
    globalFontSupport,
    timezone,
)

fun otherUser(
    id: Int = 53,
    username: String = "jane.doe",
    email: String = "jane.doe@example.com",
    color: Color? = Color("#00ff00"),
    profilePicture: Url? = Url("https://example.com/jane-doe.jpg"),
    weekStartDay: Int? = 0,
    globalFontSupport: Boolean? = true,
    timezone: String? = "Europe/London",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    weekStartDay,
    globalFontSupport,
    timezone,
)
