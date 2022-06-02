package com.bkahlert.kommons.serialization

import com.bkahlert.hello.clickup.api.User
import com.bkahlert.hello.clickup.api.UserID
import com.bkahlert.hello.clickup.api.user
import com.bkahlert.hello.debug.clickup.ClickUpFixtures
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable

@Suppress("unused")
class NamedSerializerTest {

    inner class OfSingle {

        inner class OfNull : SerializerTest<Named<User?>>(
            NamedSerializer(User.serializer().nullable),
            // language=JSON
            """
                {
                    "data": null
                }
            """.trimIndent() to Named.data<User?>(null)
        )

        inner class OfNonNull : SerializerTest<Named<User>>(
            NamedSerializer(User.serializer()),
            // language=JSON
            """
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
                }
            """.trimIndent() to Named.ofSingle(user())
        )
    }

    inner class OfMultiple : SerializerTest<Named<List<User>>>(
        NamedSerializer(ListSerializer(User.serializer())),
        // language=JSON
        """
            {
                "users": [
                    {
                        "id": 11111,
                        "username": "john.doe",
                        "email": "john.doe@example.com",
                        "color": "#ff0000",
                        "profilePicture": "${ClickUpFixtures.User.profilePicture}",
                        "initials": "JD",
                        "week_start_day": 1,
                        "global_font_support": false,
                        "timezone": "Europe/Berlin"
                    },
                    {
                        "id": 43,
                        "username": "john.doe",
                        "email": "john.doe@example.com",
                        "color": "#ff0000",
                        "profilePicture": "${ClickUpFixtures.User.profilePicture}",
                        "initials": "JD",
                        "week_start_day": 1,
                        "global_font_support": false,
                        "timezone": "Europe/Berlin"
                    }
                ]
            }
        """.trimIndent() to Named.ofMultiple(listOf(user(), user(id = UserID(43))))
    )
}
