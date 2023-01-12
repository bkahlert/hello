package com.bkahlert.hello.clickup.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CustomFieldFilter(
    @SerialName("field_id") val id: String,
    val operator: Operator,
    val values: List<String>,
) {
    @Serializable
    public enum class Operator {
        @SerialName("=") Equals,
        @SerialName("<") LessThan,
        @SerialName("<=") LessThanOrEqualTo,
        @SerialName(">") GreaterThan,
        @SerialName(">=") GreaterThanOrEqualTo,
        @SerialName("!=") NotEquals,
        @SerialName("IS NULL") IsNull,
        @SerialName("IS NOT NULL") IsNotNull,
        @SerialName("RANGE") Range,
        @SerialName("ANY") `Any`,
        @SerialName("ALL") All,
        @SerialName("NOT ANY") NotAny,
        @SerialName("NOT ALL") NotAll,
    }
}
