package com.bkahlert.hello.app.showcase

import com.bkahlert.hello.app.widgets.DefaultWidgetRegistration
import com.bkahlert.hello.app.widgets.DefaultWidgets
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.syncStoreOf
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.Widgets
import com.bkahlert.hello.widget.image.ImageWidget
import com.bkahlert.hello.widget.video.VideoWidget
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.storeOf

public val WidgetShowcases: SimplePage = SimplePage(
    "widgets",
    "Widgets",
    "Widgets showcases",
    heroIcon = HeroIcons::squares_plus,
) {

    val store: SyncStore<List<Widget>> = syncStoreOf(
        storeOf(
            DefaultWidgets + listOf(
                ImageWidget(
                    id = "nyan-cat",
                    src = Uri("nyancat.svg"),
                    aspectRatio = AspectRatio.video
                ),
                VideoWidget(
                    id = "rick-astley",
                    title = "Rick Astley",
                    src = Uri("https://www.youtube.com/embed/dQw4w9WgXcQ"),
                ),
            )
        )
    )
    val widgets = Widgets(store, DefaultWidgetRegistration)

    widgets.render(this)
}
