package com.bkahlert.hello.clickup

import com.bkahlert.kommons.Color
import com.bkahlert.kommons.serialization.ColorSerializer
import com.bkahlert.kommons.serialization.UrlSerializer
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    val name: String,
    @Serializable(ColorSerializer::class)
    val color: Color,
    @Serializable(UrlSerializer::class)
    val avatar: Url,
    val members: List<BoxedUser>,
)
