import com.bkahlert.hello.clickup.BoxedUser
import com.bkahlert.hello.clickup.user
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BoxedUserTest {
    val json =
        // language=JSON
        """
            {
                "user": {
                    "id": 42,
                    "username": "john.doe",
                    "email": "john.doe@example.com",
                    "color": "#ff0000",
                    "profilePicture": "https://example.com/john-doe.jpg",
                    "week_start_day": 1,
                    "global_font_support": false,
                    "timezone": "Europe/Berlin"
                }
            }
        """.trimIndent()

    @Test fun deserialize() {
        json.deserialize<BoxedUser>() shouldBe user().box()
    }

    @Test fun serialize() {
        user().box().serialize() shouldBe json
    }
}
