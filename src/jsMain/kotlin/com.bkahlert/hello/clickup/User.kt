package com.bkahlert.hello.clickup

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val color: String, // TODO color
    val profilePicture: String,  // TODO URL
    val week_start_day: Int,
    val global_font_support: Boolean,
    val timezone: String,  // TODO timezone
)
