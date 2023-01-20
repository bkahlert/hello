package com.bkahlert.hello.clickup.model.fixtures

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ImageFixturesTest {

    @Test
    fun spacer() {
        ImageFixtures.Spacer.toString() shouldBe "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7"
    }
}
