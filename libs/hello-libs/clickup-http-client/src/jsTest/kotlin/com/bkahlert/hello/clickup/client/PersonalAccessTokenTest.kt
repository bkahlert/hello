package com.bkahlert.hello.clickup.client

import com.bkahlert.hello.clickup.client.http.PersonalAccessToken
import com.bkahlert.kommons.test.testAll
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test

class PersonalAccessTokenTest {

    @Test
    fun instantiation() = testAll {
        PersonalAccessToken(VALID_CLICKUP_API_TOKEN).token shouldBe VALID_CLICKUP_API_TOKEN
        shouldThrow<IllegalArgumentException> { PersonalAccessToken(INVALID_CLICKUP_API_TOKEN) }.message shouldContain "must match"
    }

    @Test
    fun deserialize() = testAll {
        Json.decodeFromJsonElement<PersonalAccessToken>(PersonalAccessToken.ValidApiTokenJsonElement) shouldBe PersonalAccessToken.ValidApiToken
        shouldThrow<IllegalArgumentException> { Json.decodeFromJsonElement<PersonalAccessToken>(PersonalAccessToken.InvalidApiTokenJsonElement) }
    }

    @Test
    fun serialize() = testAll {
        Json.encodeToJsonElement(PersonalAccessToken.ValidApiToken) shouldBe PersonalAccessToken.ValidApiTokenJsonElement
    }
}

const val VALID_CLICKUP_API_TOKEN = "pk_123_abc"
const val INVALID_CLICKUP_API_TOKEN = "foo"

val PersonalAccessToken.Companion.ValidApiToken
    get() = PersonalAccessToken(VALID_CLICKUP_API_TOKEN)
val PersonalAccessToken.Companion.ValidApiTokenJsonElement
    get() = JsonPrimitive(VALID_CLICKUP_API_TOKEN)

val PersonalAccessToken.Companion.InvalidApiTokenJsonElement
    get() = JsonPrimitive(INVALID_CLICKUP_API_TOKEN)
