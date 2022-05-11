import com.bkahlert.hello.plugins.clickup.otherUser
import com.bkahlert.hello.plugins.clickup.user
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures
import com.bkahlert.kommons.color.Color
import com.bkahlert.kommons.color.Color.RGB
import com.bkahlert.kommons.dom.URL
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.SerializerTest
import com.clickup.api.Team
import com.clickup.api.TeamID
import com.clickup.api.User

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
    avatar: URL = URL.parse("https://example.com/work.png"),
    vararg members: User = arrayOf(user(), otherUser()),
) = Team(
    id,
    name,
    color,
    avatar,
    members.map { Named.ofSingle(it) },
)
