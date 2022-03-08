import com.bkahlert.hello.clickup.BoxedTeams
import com.bkahlert.hello.clickup.BoxedUser
import com.bkahlert.hello.clickup.Team
import com.bkahlert.hello.clickup.User
import com.bkahlert.hello.clickup.otherUser
import com.bkahlert.hello.deserialize
import com.bkahlert.hello.serialize
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class BoxedTeamsTest : ShouldSpec({
    val json =
        // language=JSON
        """
            {
                "teams": [
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
                    },
                    {
                        "id": 4829,
                        "name": "work group",
                        "color": "#0000ff",
                        "avatar": "https://example.com/work.png",
                        "members": [
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
                ]
            }
        """.trimIndent()
    should("deserialize") {
        json.deserialize<BoxedTeams>() shouldBe boxedTeams()
    }
    should("serialize") {
        boxedTeams().serialize() shouldBe json
    }

    should("deserialize real world data") {
        """
            {"teams":[{"id":"257831","name":"BKAHLERT","color":"#e040fb","avatar":"https://attachments2.clickup.com/team_avatars/257831_i9f.blob?Expires=1661904000&Key-Pair-Id=APKAIYJRUKB5RLQWHZ...3VmUzJohxatUBhTx9JQxN8BCOTA__","members":[{"user":{"id":468596,"username":"Björn Kahlert","email":"mail@bkahlert.tld","color":"#4169E1","profilePicture":"https://attachments.clickup.com/profilePictures/468596_ARW.jpg","initials":"BK","role":1,"custom_role":null,"last_active":"1646622890760","date_joined":"1595175635526","date_invited":"1595175635526"}}]}]}
        """.trimIndent().deserialize<BoxedTeams>() shouldBe BoxedTeams(listOf(
            Team(257831,
                "BKAHLERT",
                "#e040fb",
                "https://attachments2.clickup.com/team_avatars/257831_i9f.blob?Expires=1661904000&Key-Pair-Id=APKAIYJRUKB5RLQWHZ...3VmUzJohxatUBhTx9JQxN8BCOTA__",
                listOf(BoxedUser(User(
                    468596,
                    "Björn Kahlert",
                    "mail@bkahlert.tld",
                    "#4169E1",
                    "https://attachments.clickup.com/profilePictures/468596_ARW.jpg",
                    null,
                    null,
                    null,
                )
                )))))
    }
})

fun boxedTeams(
    vararg teams: Team = arrayOf(team(), team(id = 4829, members = arrayOf(otherUser()))),
) = BoxedTeams(teams.toList())
