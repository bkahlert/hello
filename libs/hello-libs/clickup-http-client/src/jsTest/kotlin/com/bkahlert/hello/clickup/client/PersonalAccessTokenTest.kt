package com.bkahlert.hello.clickup.client

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class PersonalAccessTokenTest {

    @Test
    fun instantiation() = testAll {
        PersonalAccessToken("pk_123_abc").token shouldBe "pk_123_abc"

        shouldThrow<IllegalArgumentException> {
            PersonalAccessToken("foo")
        }.message shouldContain "must match"
    }
}
