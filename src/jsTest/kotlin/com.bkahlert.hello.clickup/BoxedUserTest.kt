import com.bkahlert.hello.clickup.BoxedUser
import com.bkahlert.hello.clickup.user
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class BoxedUserTest : ShouldSpec({
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
    should("deserialize") {
        json.deserialize<BoxedUser>() shouldBe user().box()
    }
    should("serialize") {
        user().box().serialize() shouldBe json
    }
})
