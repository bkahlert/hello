package com.bkahlert.kommons.auth

import com.bkahlert.kommons.test.testAll
import com.bkahlert.kommons.uri.Uri
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test

class ClaimsTest {

    @Test
    fun serialize() = testAll {
        Json.encodeToString(CLAIMS) shouldEqualJson CLAIMS_STRING
    }

    @Test
    fun deserialization() = testAll {
        Json.decodeFromString<JsonObjectClaims>(CLAIMS_STRING) shouldBe CLAIMS
    }

    @Test
    fun extensions() = testAll {
        CLAIMS should {
            it.subjectIdentifier shouldBe "user.id"
            it.name shouldBe "John Doe"
            it.givenName shouldBe "John"
            it.familyFame shouldBe "Doe"
            it.middleName shouldBe null
            it.nickname shouldBe null
            it.preferredUsername shouldBe "john.doe"
            it.profile shouldBe Uri.parse("https://example.com/john.doe")
            it.picture shouldBe Uri.parse("data:image/gif;base64,R0lGODdhAQADAPABAP////8AACwAAAAAAQADAAACAgxQADs")
            it.website shouldBe null
            it.email shouldBe "john.doe@example.com"
            it.emailVerified shouldBe true
            it.gender shouldBe "male"
            it.birthdate shouldBe "0000-05-15"
            it.zoneInfo shouldBe null
            it.locale shouldBe "de_EN"
            it.phoneNumber shouldBe "+1 23 456"
            it.phoneNumberVerified shouldBe false
            it.address shouldBe JsonPrimitive("Berlin, Germany")
            it.updatedAt shouldBe Instant.fromEpochSeconds(1676944054)
        }
    }
}

// language=json
const val CLAIMS_STRING = """{
    "sub": "user.id",
    "name": "John Doe",
    "given_name": "John",
    "family_name": "Doe",
    "middle_name": null,
    "preferred_username": "john.doe",
    "profile": "https://example.com/john.doe",
    "picture": "data:image/gif;base64,R0lGODdhAQADAPABAP////8AACwAAAAAAQADAAACAgxQADs",
    "email": "john.doe@example.com",
    "email_verified": true,
    "gender": "male",
    "birthdate": "0000-05-15",
    "locale": "de_EN",
    "phone_number": "+1 23 456",
    "phone_number_verified": false,
    "address": "Berlin, Germany",
    "updated_at": 1676944054            
}"""

val CLAIMS = Claims(
    "sub" to JsonPrimitive("user.id"),
    "name" to JsonPrimitive("John Doe"),
    "given_name" to JsonPrimitive("John"),
    "family_name" to JsonPrimitive("Doe"),
    "middle_name" to JsonNull,
    "preferred_username" to JsonPrimitive("john.doe"),
    "profile" to JsonPrimitive("https://example.com/john.doe"),
    "picture" to JsonPrimitive("data:image/gif;base64,R0lGODdhAQADAPABAP////8AACwAAAAAAQADAAACAgxQADs"),
    "email" to JsonPrimitive("john.doe@example.com"),
    "email_verified" to JsonPrimitive(true),
    "gender" to JsonPrimitive("male"),
    "birthdate" to JsonPrimitive("0000-05-15"),
    "locale" to JsonPrimitive("de_EN"),
    "phone_number" to JsonPrimitive("+1 23 456"),
    "phone_number_verified" to JsonPrimitive(false),
    "address" to JsonPrimitive("Berlin, Germany"),
    "updated_at" to JsonPrimitive(1676944054),
)
