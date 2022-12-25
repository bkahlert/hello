package com.bkahlert.kommons.deployment

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class BackendInfoTest {

    @Test
    fun name() {
        BackendInfo.hostedUiUrl shouldBe "abc"
    }
}
