package com.bkahlert.hello.applet

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.ContentBuilder1
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.kommons.uri.Uri
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonNames
import org.w3c.dom.Element

public enum class AspectRatio(
    public val title: String,
    public val icon: Uri,
    public val classes: String? = null,
    public val wrap: ContentBuilder1<Element, ContentBuilder<Element>> = { it() },
) {
    @SerialName("video")
    @JsonNames("16:9")
    video(
        title = "Video",
        icon = SolidHeroIcons.video_camera,
        wrap = { div("aspect-w-16 aspect-h-9") { it() } }
    ),

    @SerialName("stretch")
    @JsonNames("full", "fill")
    stretch(
        title = "Stretch",
        icon = SolidHeroIcons.arrows_pointing_out,
        classes = "w-full h-full grid items-stretch content-stretch"
    ),

    @SerialName("none")
    none(
        title = "None",
        icon = OutlineHeroIcons.stop,
    ),
}
