package com.bkahlert.kommons.js

import AccessTokenBasedClickUpClient
import com.bkahlert.kommons.dom.InMemoryStorage
import com.clickup.api.rest.AccessToken
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import kotlin.js.Json
import kotlin.js.json
import kotlin.test.Test

class JsonTest {

    private fun nativeObject(): Json =
        js("(function() { function Function() { this.property = \"Function-property\" }; return new Function(); })()").unsafeCast<Json>()

    private fun nativeInstance(): Json =
        js("eval('(function() { $BaseClassJS; $OrdinaryClassJS; return new OrdinaryClass(); })()')").unsafeCast<Json>()

    @Suppress("unused")
    private open class BaseClass {
        val baseProperty: String = "base-property"
        open val openBaseProperty: Int = 42
        protected open val protectedOpenBaseProperty: String = "protected-open-base-property"
        private val privateBaseProperty: String = "private-base-property"
    }

    @Suppress("unused")
    private class OrdinaryClass : BaseClass() {
        val ordinaryProperty: String = "ordinary-property"
        private val privateOrdinaryProperty: String = "private-ordinary-property"
    }

    private data class DataClass(
        val dataProperty: String = "data-property",
        override val openBaseProperty: Int = 37,
    ) : BaseClass() {
        override val protectedOpenBaseProperty: String = "overridden-protected-open-base-property"
        @Suppress("unused") private val privateDataProperty: String = "private-data-property"
    }

    @Test fun object_keys() {
        Object.keys(nativeObject()) shouldBe arrayOf(
            "property",
        )
        Object.keys(nativeInstance()) shouldBe arrayOf(
            "baseProperty",
            "ordinaryProperty",
        )
        Object.keys(BaseClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1"
        )
        Object.keys(OrdinaryClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "ordinaryProperty_1",
            "privateOrdinaryProperty_1"
        )
        Object.keys(DataClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "dataProperty_1",
            "openBaseProperty_2",
            "protectedOpenBaseProperty_2",
            "privateDataProperty_1"
        )
    }

    @Test fun object_entries() {
        Object.entries(nativeObject()) shouldBe arrayOf(
            arrayOf("property", "Function-property"),
        )
        Object.entries(nativeInstance()) shouldBe arrayOf(
            arrayOf("baseProperty", "base-property"),
            arrayOf("ordinaryProperty", "ordinary-property"),
        )
        Object.entries(BaseClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
        )
        Object.entries(OrdinaryClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
            arrayOf("ordinaryProperty_1", "ordinary-property"),
            arrayOf("privateOrdinaryProperty_1", "private-ordinary-property"),
        )
        Object.entries(DataClass()) shouldBe arrayOf(
            arrayOf("baseProperty_1", "base-property"),
            arrayOf("openBaseProperty_1", 42),
            arrayOf("protectedOpenBaseProperty_1", "protected-open-base-property"),
            arrayOf("privateBaseProperty_1", "private-base-property"),
            arrayOf("dataProperty_1", "data-property"),
            arrayOf("openBaseProperty_2", 37),
            arrayOf("protectedOpenBaseProperty_2", "overridden-protected-open-base-property"),
            arrayOf("privateDataProperty_1", "private-data-property"),
        )
    }

    @Test fun object_get_own_property_names() {
        Object.getOwnPropertyNames(nativeObject()) shouldBe arrayOf(
            "property",
        )
        Object.getOwnPropertyNames(nativeInstance()) shouldBe arrayOf(
            "baseProperty",
            "ordinaryProperty",
        )
        Object.getOwnPropertyNames(BaseClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1"
        )
        Object.getOwnPropertyNames(OrdinaryClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "ordinaryProperty_1",
            "privateOrdinaryProperty_1"
        )
        Object.getOwnPropertyNames(DataClass()) shouldBe arrayOf(
            "baseProperty_1",
            "openBaseProperty_1",
            "protectedOpenBaseProperty_1",
            "privateBaseProperty_1",
            "dataProperty_1",
            "openBaseProperty_2",
            "protectedOpenBaseProperty_2",
            "privateDataProperty_1"
        )
    }

    @Test fun object_keys_extension() {
        nativeObject().keys shouldBe Object.keys(nativeObject())
        nativeInstance().keys shouldBe Object.keys(nativeInstance())
        BaseClass().keys shouldBe Object.keys(BaseClass())
        OrdinaryClass().keys shouldBe Object.keys(OrdinaryClass())
        DataClass().keys shouldBe Object.keys(DataClass())
    }

    @Test fun object_entries_extension() {
        nativeObject().entries shouldBe Object.entries(nativeObject())
        nativeInstance().entries shouldBe Object.entries(nativeInstance())
        BaseClass().entries shouldBe Object.entries(BaseClass())
        OrdinaryClass().entries shouldBe Object.entries(OrdinaryClass())
        DataClass().entries shouldBe Object.entries(DataClass())
    }

    @Test fun object_properties_extension() {
        nativeObject().properties shouldBe mapOf(
            "property" to "Function-property",
        )
        nativeInstance().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "ordinaryProperty" to "ordinary-property",
        )
        BaseClass().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
        )
        OrdinaryClass().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "ordinaryProperty" to "ordinary-property",
            "privateOrdinaryProperty" to "private-ordinary-property",
        )
        DataClass().properties shouldBe mapOf(
            "baseProperty" to "base-property",
            "openBaseProperty" to 37,
            "protectedOpenBaseProperty" to "overridden-protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "dataProperty" to "data-property",
            "privateDataProperty" to "private-data-property",
        )
    }

    @Test fun iterable_to_json() {
        emptyList<Pair<String, Any?>>().toJson().entries shouldBe json().entries
        listOf("notNull" to 42, "null" to null, "map" to mapOf("foo" to "bar")).toJson().entries shouldBe json(
            "notNull" to 42,
            "null" to null,
            "map" to mapOf("foo" to "bar"),
        ).entries
    }

    @Test fun map_to_json() {
        emptyMap<String, Any?>().toJson().entries shouldBe json().entries
        mapOf("notNull" to 42, "null" to null, "map" to mapOf("foo" to "bar")).toJson().entries shouldBe json(
            "notNull" to 42,
            "null" to null,
            "map" to mapOf("foo" to "bar"),
        ).entries
    }

    @Test fun iterable_to_json_array() {
        emptyList<Any?>().toJsonArray().entries shouldBe json().entries
        listOf(42, null, mapOf("foo" to "bar")).toJsonArray() should {
            it.size shouldBe 3
            it[0] shouldBe 42
            it[1].entries shouldBe emptyArray()
            it[2].entries shouldBe arrayOf(arrayOf("foo", "bar"))
        }
    }

    @Suppress("unused")
    @Test fun map_to_json_array() {
        emptyArray<Any?>().toJsonArray().entries shouldBe json().entries
        arrayOf(42, null, mapOf("foo" to "bar")).toJsonArray() should {
            it.size shouldBe 3
            it[0] shouldBe 42
            it[1].entries shouldBe emptyArray()
            it[2].entries shouldBe arrayOf(arrayOf("foo", "bar"))
        }
    }

    @Test fun object_stringify_extension() {
        null.stringify() shouldBe "null"
        "string".stringify() shouldBe """
            "string"
        """.trimIndent()
        arrayOf("string", 42).stringify() shouldBe """
            [
              "string",
              42
            ]
        """.trimIndent()
        listOf("string", 42).stringify() shouldBe """
            [
              "string",
              42
            ]
        """.trimIndent()
        arrayOf("string" to "value", "digit" to 42).stringify() shouldBe """
            [
              {
                "first": "string",
                "second": "value"
              },
              {
                "first": "digit",
                "second": 42
              }
            ]
        """.trimIndent()
        mapOf("string" to "value", "digit" to 42).stringify() shouldBe """
            {
              "string": "value",
              "digit": 42
            }
        """.trimIndent()
        nativeObject().stringify() shouldBe """
            {
              "property": "Function-property"
            }
            """.trimIndent()
        nativeInstance().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "ordinaryProperty": "ordinary-property"
            }
            """.trimIndent()
        BaseClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 42,
              "protectedOpenBaseProperty": "protected-open-base-property",
              "privateBaseProperty": "private-base-property"
            }
            """.trimIndent()
        OrdinaryClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 42,
              "protectedOpenBaseProperty": "protected-open-base-property",
              "privateBaseProperty": "private-base-property",
              "ordinaryProperty": "ordinary-property",
              "privateOrdinaryProperty": "private-ordinary-property"
            }
            """.trimIndent()
        DataClass().stringify() shouldBe """
            {
              "baseProperty": "base-property",
              "openBaseProperty": 37,
              "protectedOpenBaseProperty": "overridden-protected-open-base-property",
              "privateBaseProperty": "private-base-property",
              "dataProperty": "data-property",
              "privateDataProperty": "private-data-property"
            }
            """.trimIndent()
    }

    @Test fun string_parse_extension() {
        null.stringify().parse().entries shouldBe json().entries
        "string".stringify().parse().entries shouldBe "string".entries
        arrayOf("string", 42).stringify().parse().entries shouldBe json(
            "0" to "string",
            "1" to 42,
        ).entries
        listOf("string", 42).stringify().parse().entries shouldBe json(
            "0" to "string",
            "1" to 42,
        ).entries
        arrayOf("string" to "value", "digit" to 42).stringify().parse().stringify() shouldBe arrayOf(
            "string" to "value",
            "digit" to 42,
        ).stringify()
        mapOf("string" to "value", "digit" to 42).stringify().parse().entries shouldBe json(
            "string" to "value",
            "digit" to 42
        ).entries
        nativeObject().stringify().parse().entries shouldBe json(
            "property" to "Function-property",
        ).entries
        nativeInstance().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "ordinaryProperty" to "ordinary-property",
        ).entries
        BaseClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
        ).entries
        OrdinaryClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 42,
            "protectedOpenBaseProperty" to "protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "ordinaryProperty" to "ordinary-property",
            "privateOrdinaryProperty" to "private-ordinary-property",
        ).entries
        DataClass().stringify().parse().entries shouldBe json(
            "baseProperty" to "base-property",
            "openBaseProperty" to 37,
            "protectedOpenBaseProperty" to "overridden-protected-open-base-property",
            "privateBaseProperty" to "private-base-property",
            "dataProperty" to "data-property",
            "privateDataProperty" to "private-data-property",
        ).entries
    }

    @Test fun any_to_json_extension() {
        null.toJson().entries shouldBe null.stringify().parse().entries
        "string".toJson().entries shouldBe "string".stringify().parse().entries
        arrayOf("string", 42).toJson().entries shouldBe arrayOf("string", 42).stringify().parse().entries
        listOf("string", 42).toJson().entries shouldBe listOf("string", 42).stringify().parse().entries
        arrayOf("string" to "value", "digit" to 42).toJson().stringify() shouldBe arrayOf("string" to "value", "digit" to 42).stringify().parse().stringify()
        mapOf("string" to "value", "digit" to 42).toJson().entries shouldBe mapOf("string" to "value", "digit" to 42).stringify().parse().entries
        nativeObject().toJson().entries shouldBe nativeObject().stringify().parse().entries
        nativeInstance().toJson().entries shouldBe nativeInstance().stringify().parse().entries
        BaseClass().toJson().entries shouldBe BaseClass().stringify().parse().entries
        OrdinaryClass().toJson().entries shouldBe OrdinaryClass().stringify().parse().entries
        DataClass().toJson().entries shouldBe DataClass().stringify().parse().entries
    }

    @Test fun special_to_json_extension() {
        AccessTokenBasedClickUpClient(AccessToken("pk_123_abc"), InMemoryStorage()).stringify() shouldStartWith """
            {
              "accessToken": "pk_123_abc",
        """.trimIndent()
    }

    companion object {
        const val BaseClassJS = "class BaseClass{ constructor() { this.baseProperty = \"base-property\"; } }"
        const val OrdinaryClassJS = "class OrdinaryClass extends BaseClass { constructor() { super(); this.ordinaryProperty = \"ordinary-property\"; } }"
    }
}
