package com.bkahlert.hello.clickup

import com.bkahlert.hello.ui.demo.ImageFixtures
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures
import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.SerializerTest
import com.clickup.api.User
import com.clickup.api.UserID
import org.w3c.dom.url.URL

@Suppress("unused")
class UserTest : SerializerTest<User>(User.serializer(), ClickUpFixtures.UserJson to user())

fun user(
    id: UserID = UserID(11111),
    username: String = "john.doe",
    email: String = "john.doe@example.com",
    color: Color = Color("#ff0000"),
    profilePicture: URL = URL(ImageFixtures.JohnDoe),
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
    color: Color = Color("#00ff00"),
    profilePicture: URL = URL("https://example.com/jane-doe.jpg"),
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
