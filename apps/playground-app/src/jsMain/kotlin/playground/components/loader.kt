package playground.components

import com.bkahlert.hello.fritz2.components.screenReaderOnly
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.AriaReferenceHook
import dev.fritz2.headless.foundation.hook
import org.w3c.dom.HTMLDivElement

fun RenderContext.loader(
    text: String = "Loading...",
) {
    val ariaLabelId = AriaReferenceHook<Tag<HTMLDivElement>>(Aria.labelledby)
    div("animate-spin inline-block w-4 h-4 border-[3px] border-current border-t-transparent text-blue-600 rounded-full") {
        attr("role", Aria.Role.status)
        hook(ariaLabelId)
        screenReaderOnly(ariaLabelId("title")) { +text }
    }
}
