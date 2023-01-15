package com.bkahlert.hello.clickup.model.fixtures

import com.bkahlert.kommons.json.LenientJson
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.encodeToString
import kotlin.test.Test

class ClickUpFixturesTest {

    @Test
    fun user() {
        LenientJson.encodeToString(ClickUpFixtures.User) shouldEqualJson ClickUpFixtures.UserJson
    }
}
