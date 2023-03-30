package com.bkahlert.hello.fritz2.components.embed

import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.Serializable

@Serializable
public data class EmbedProps(
    val uri: Uri,
    val attributes: Map<String, String?> = emptyMap(),
) {
    public companion object {
        public val Default: EmbedProps = EmbedProps(
            uri = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
            attributes = mapOf(
                "allow" to "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture",
                "allowfullscreen" to "true",
            )
        )
    }
}
