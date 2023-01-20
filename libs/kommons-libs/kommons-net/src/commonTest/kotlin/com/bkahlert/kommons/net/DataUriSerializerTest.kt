package com.bkahlert.kommons.net

import com.bkahlert.kommons.test.testAll
import io.kotest.matchers.shouldBe
import io.ktor.http.quote
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class DataUriSerializerTest {

    @Test fun deserialize() = testAll {
        Json.Default.decodeFromString<DataUri>("data:;base64,Rm9vIGJhcg".quote()) shouldBe DataUri.parse("data:;base64,Rm9vIGJhcg")
    }

    @Test fun serialize() = testAll {
        Json.Default.encodeToString(DataUri.parse("data:;base64,Rm9vIGJhcg")) shouldBe "data:;base64,Rm9vIGJhcg".quote()
    }
}
