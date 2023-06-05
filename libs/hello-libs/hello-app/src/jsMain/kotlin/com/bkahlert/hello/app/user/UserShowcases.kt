package com.bkahlert.hello.app.user

import com.bkahlert.hello.app.session.FakeSession
import com.bkahlert.hello.app.session.SessionStore
import com.bkahlert.hello.app.session.TestUserInfo
import com.bkahlert.hello.icon.assets.Images.JohnDoeWithBackground
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.showcase.showcase
import com.bkahlert.hello.showcase.showcases
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import kotlinx.serialization.json.JsonPrimitive

public object UserShowcases : SimplePage(
    "user",
    "User",
    "User showcases",
    heroIcon = HeroIcons::user_circle,
    content = {
        showcases("UserDropdown", icon = SolidHeroIcons.user_circle) {
            showcase("Unauthorized", resizable = false) {
                div("flex items-center justify-end p-4") { userDropdown(SessionStore(FakeSession.Unauthorized())) }
            }
            showcase("Authorized (without picture)", resizable = false) {
                div("flex items-center justify-end p-4") { userDropdown(SessionStore(FakeSession.Authorized())) }
            }
            showcase("Authorized (with username and picture)", resizable = false) {
                div("flex items-center justify-end p-4") {
                    userDropdown(
                        SessionStore(
                            FakeSession.Authorized(
                                TestUserInfo {
                                    put(User.USERNAME_CLAIM_NAME, JsonPrimitive("john.doe"))
                                    put(OpenIDStandardClaims.PICTURE_CLAIM_NAME, JsonPrimitive(JohnDoeWithBackground.toString()))
                                },
                            ),
                        )
                    )
                }
            }
        }
    }
)
