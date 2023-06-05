package com.bkahlert.hello.app.widgets

import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.image.ImageWidget
import com.bkahlert.hello.widget.preview.FeaturePreview
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.hello.widget.video.VideoWidget
import com.bkahlert.hello.widget.website.WebsiteWidget
import com.bkahlert.kommons.uri.Uri

public val DefaultWidgets: List<Widget> by lazy {
    buildList {
        add(
            ImageWidget(
                id = "nyan-cat",
                title = "Nyan Cat",
                src = Uri("https://raw.githubusercontent.com/bkahlert/-/master/nyancat.svg"),
                aspectRatio = AspectRatio.video
            )
        )
        add(
            VideoWidget(
                id = "rick-astley",
                title = "Rick Astley",
                src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
            )
        )
        add(
            WebsiteWidget(
                id = "impossible-color",
                title = "Impossible color",
                src = Uri("https://en.wikipedia.org/wiki/Impossible_color"),
                aspectRatio = AspectRatio.stretch,
            )
        )
        FeaturePreview.values().mapTo(this) {
            FeaturePreviewWidget(
                id = "feature-preview-${it.name}",
                feature = it,
                aspectRatio = AspectRatio.stretch,
            )
        }
    }
}
