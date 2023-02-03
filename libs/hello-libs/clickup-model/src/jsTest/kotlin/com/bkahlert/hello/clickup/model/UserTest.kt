package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.model.fixtures.ImageFixtures
import com.bkahlert.hello.clickup.serialization.SerializerTest
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.uri.Uri

@Suppress("unused")
class UserTest : SerializerTest<User>(
    User.serializer(),
    ClickUpFixtures.UserJson to user(),
)

fun user(
    id: UserID = UserID(11111),
    username: String = "john.doe",
    email: String = "john.doe@example.com",
    color: Color = Color("#ff0000"),
    profilePicture: Uri = Uri.parse(ImageFixtures.JohnDoe),
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
    profilePicture: Uri = Uri.parse("https://example.com/jane-doe.jpg"),
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
