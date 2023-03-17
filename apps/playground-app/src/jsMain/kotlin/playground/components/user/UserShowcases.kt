@file:Suppress("RedundantVisibilityModifier")

package playground.components.user

import com.bkahlert.hello.session.demo.FakeSessionDataSource
import com.bkahlert.hello.session.demo.TestUserInfo
import com.bkahlert.hello.user.domain.User
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import com.bkahlert.semanticui.demo.SemanticUiImageFixtures
import kotlinx.serialization.json.JsonPrimitive
import playground.components.Page
import playground.components.session.SessionStore
import playground.components.showcase.showcase
import playground.components.showcase.showcases
import playground.tailwind.heroicons.HeroIcons

public object UserShowcases : Page(
    "user",
    "User",
    "User showcases",
    heroIcon = HeroIcons::user_circle,
    content = {
        showcases("UserDropdown") {
            showcase("Unauthorized") {
                div("flex items-center justify-end") { userDropdown(UserStore(SessionStore(FakeSessionDataSource()))) }
            }
            showcase("Authorized (without picture)") {
                div("flex items-center justify-end") {
                    userDropdown(
                        UserStore(
                            SessionStore(
                                FakeSessionDataSource(initiallyAuthorized = true),
                            )
                        )
                    )
                }
            }
            showcase("Authorized (with username and picture)") {
                div("flex items-center justify-end") {
                    userDropdown(
                        UserStore(
                            SessionStore(
                                FakeSessionDataSource(
                                    TestUserInfo {
                                        put(User.USERNAME_CLAIM_NAME, JsonPrimitive("john.doe"))
                                        put(OpenIDStandardClaims.PICTURE_CLAIM_NAME, JsonPrimitive(SemanticUiImageFixtures.JohnDoeWithBackground.toString()))
                                    },
                                    initiallyAuthorized = true
                                ),
                            )
                        )
                    )
                }
            }
        }
    }
)
