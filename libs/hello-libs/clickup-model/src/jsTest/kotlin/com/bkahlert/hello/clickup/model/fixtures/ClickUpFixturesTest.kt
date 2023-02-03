package com.bkahlert.hello.clickup.model.fixtures

import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class ClickUpFixturesTest {

    @Test
    fun user() {
        Json.encodeToString(ClickUpFixtures.User) shouldEqualJson ClickUpFixtures.UserJson
    }
}
