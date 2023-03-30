@file:Suppress("RedundantVisibilityModifier")

package com.bkahlert.hello.fritz2.app.user

import com.bkahlert.hello.fritz2.app.session.FakeSession
import com.bkahlert.hello.fritz2.app.session.SessionStore
import com.bkahlert.hello.fritz2.app.session.TestUserInfo
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.assets.Images.JohnDoeWithBackground
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import kotlinx.serialization.json.JsonPrimitive

public object UserShowcases : Page(
    "user",
    "User",
    "User showcases",
    heroIcon = HeroIcons::user_circle,
    pageContent = {
        showcases("UserDropdown") {
            showcase("Unauthorized") {
                div("flex items-center justify-end") { userDropdown(SessionStore(FakeSession.Unauthorized())) }
            }
            showcase("Authorized (without picture)") {
                div("flex items-center justify-end") { userDropdown(SessionStore(FakeSession.Authorized())) }
            }
            showcase("Authorized (with username and picture)") {
                div("flex items-center justify-end") {
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
