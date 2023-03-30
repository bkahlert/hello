package playground.components.app

import com.bkahlert.hello.clickup.clickUpMenu
import com.bkahlert.hello.clickup.clickUpProps
import com.bkahlert.hello.fritz2.app.AppStore
import com.bkahlert.hello.fritz2.app.get
import com.bkahlert.hello.fritz2.app.props
import dev.fritz2.core.RenderContext
import kotlinx.coroutines.flow.map

fun RenderContext.clickUpApp(
    store: AppStore,
) {
    app(store) {
        store.data.props["clickup"].map { it?.clickUpProps }.render {
            clickUpMenu(it)
        }
    }
}
