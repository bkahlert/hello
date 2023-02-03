package com.bkahlert.kommons.auth

import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.uri.Uri
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class OpenIDProviderTest {

    @Test
    fun open_id_configuration_uri() = testAll {
        OpenIDProvider(Uri.parse("https://provider.example.com")).openIDConfigurationUri
            .shouldBe(Uri.parse("https://provider.example.com/.well-known/openid-configuration"))
        OpenIDProvider(Uri.parse("https://provider.example.com/")).openIDConfigurationUri
            .shouldBe(Uri.parse("https://provider.example.com/.well-known/openid-configuration"))
        OpenIDProvider(Uri.parse("https://provider.example.com/path")).openIDConfigurationUri
            .shouldBe(Uri.parse("https://provider.example.com/path/.well-known/openid-configuration"))
        OpenIDProvider(Uri.parse("https://provider.example.com/path/")).openIDConfigurationUri
            .shouldBe(Uri.parse("https://provider.example.com/path/.well-known/openid-configuration"))
    }
}
