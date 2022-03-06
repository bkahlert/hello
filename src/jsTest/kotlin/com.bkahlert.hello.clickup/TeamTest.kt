import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.otherUser
import com.bkahlert.hello.clickup.user
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import com.bkahlert.kommons.debug.trace
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
                  "${user().serialize()}}",
                  "${otherUser().serialize()}}"
                ]
            }
        """.trimIndent().trace
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
    members.toList(),
)
