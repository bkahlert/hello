import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.otherUser
import com.bkahlert.hello.clickup.user
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class TeamTest : ShouldSpec({
    val json =
        // language=JSON
        """
            {
                "id": 180,
                "name": "work group",
                "color": "#0000ff",
                "avatar": "https://example.com/work.png",
                "members": [
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
                    },
                    {
                        "user": {
                            "id": 53,
                            "username": "jane.doe",
                            "email": "jane.doe@example.com",
                            "color": "#00ff00",
                            "profilePicture": "https://example.com/jane-doe.jpg",
                            "week_start_day": 0,
                            "global_font_support": true,
                            "timezone": "Europe/London"
                        }
                    }
                ]
            }
        """.trimIndent()
    should("deserialize") {
        json.deserialize<Team>() shouldBe team()
    }
    should("serialize") {
        team().serialize() shouldBe json
    }
})

fun team(
    id: Int = 180,
    name: String = "work group",
    color: String = "#0000ff",
    avatar: String = "https://example.com/work.png",
    vararg members: User = arrayOf(user(), otherUser()),
) = Team(
    id,
    name,
    color,
    avatar,
    members.map { it.box() },
)
