package playground

import com.bkahlert.hello.app.widgets.DefaultWidgetRegistration
import com.bkahlert.hello.chatbot.ChatbotWidget
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.syncStoreOf
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.page.SimplePage
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.Widgets
import com.bkahlert.hello.widget.preview.FeaturePreview
import com.bkahlert.hello.widget.preview.FeaturePreviewWidget
import com.bkahlert.hello.widget.ssh.WsSshWidget
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.storeOf

val PlaygroundContainer = SimplePage(
    id = "playground",
    label = "Playground",
    description = "A place to play around with UI elements",
    heroIcon = HeroIcons::beaker,
) {
    val store: SyncStore<List<Widget>> = syncStoreOf(
        storeOf(
            listOf(
                FeaturePreviewWidget("xxx", feature = FeaturePreview.chatbot, AspectRatio.stretch),
            )
        )
    )
    Widgets(store, DefaultWidgetRegistration).render(this)
}
