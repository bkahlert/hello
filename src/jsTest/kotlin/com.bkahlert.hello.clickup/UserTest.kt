package com.bkahlert.hello.clickup

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class UserTest : ShouldSpec({
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
    should("deserialize") {
        json.deserialize<User>() shouldBe user()
    }
    should("serialize") {
        user().serialize() shouldBe json
    }
})

fun user(
    id: Int = 42,
    username: String = "john.doe",
    email: String = "john.doe@example.com",
    color: String = "#ff0000",
    profilePicture: String = "https://example.com/john-doe.jpg",
    week_start_day: Int = 1,
    global_font_support: Boolean = false,
    timezone: String = "Europe/Berlin",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    week_start_day,
    global_font_support,
    timezone,
)

fun otherUser(
    id: Int = 53,
    username: String = "jane.doe",
    email: String = "jane.doe@example.com",
    color: String = "#00ff00",
    profilePicture: String = "https://example.com/jane-doe.jpg",
    week_start_day: Int = 0,
    global_font_support: Boolean = true,
    timezone: String = "Europe/London",
) = User(
    id,
    username,
    email,
    color,
    profilePicture,
    week_start_day,
    global_font_support,
    timezone,
)
