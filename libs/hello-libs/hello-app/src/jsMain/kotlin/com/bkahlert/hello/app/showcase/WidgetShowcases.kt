package com.bkahlert.hello.app.showcase

import com.bkahlert.hello.app.widgets.DefaultWidgetRegistration
import com.bkahlert.hello.app.widgets.DefaultWidgets
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.syncStoreOf
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.Widgets
import dev.fritz2.core.storeOf

public val WidgetShowcases: SimplePage = SimplePage(
    "widgets",
    "Widgets",
    "Widgets showcases",
    heroIcon = HeroIcons::squares_plus,
) {

    val store: SyncStore<List<Widget>> = syncStoreOf(storeOf(DefaultWidgets))
    val widgets = Widgets(store, DefaultWidgetRegistration)

    widgets.render(this)
}
