package com.bkahlert.kommons.auth

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class OpenIDProviderTest {

    @Test
    fun open_id_configuration_uri() = testAll {
        OpenIDProvider("https://provider.example.com").openIDConfigurationUri shouldBe "https://provider.example.com/.well-known/openid-configuration"
    }
}
