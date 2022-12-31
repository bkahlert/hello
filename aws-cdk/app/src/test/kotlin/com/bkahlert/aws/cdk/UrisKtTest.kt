package com.bkahlert.aws.cdk

import com.bkahlert.kommons.test.junit.testEach
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.TestFactory

class UrisKtTest {

    val tokenUrl = "https://\${Token[TOKEN.624]}.execute-api.us-east-1.\${Token[AWS.URLSuffix.8]}/\${Token[TOKEN.632]}"
    val tokenUrls = arrayOf(tokenUrl, "$tokenUrl/")

    @TestFactory
    fun domain() = testEach(*tokenUrls) {
        it.domain shouldBe "\${Token[TOKEN.624]}.execute-api.us-east-1.\${Token[AWS.URLSuffix.8]}"
    }

    @TestFactory
    fun path() = testEach(*tokenUrls) {
        it.path shouldBe "/\${Token[TOKEN.632]}"
    }
}
