package com.bkahlert.hello.fritz2

import dev.fritz2.core.WithDomNode
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollBehavior
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollIntoViewOptionsInit
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollMode
import dev.fritz2.headless.foundation.utils.scrollintoview.ScrollPosition
import dev.fritz2.headless.foundation.utils.scrollintoview.scrollIntoView
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.SMOOTH
import org.w3c.dom.ScrollToOptions

public fun WithDomNode<Node>.scrollIntoView(
    behavior: ScrollBehavior? = ScrollBehavior.smooth,
    mode: ScrollMode? = ScrollMode.always,
    block: ScrollPosition? = ScrollPosition.center,
    inline: ScrollPosition? = ScrollPosition.center,
) {
    domNode.scrollIntoView(
        behavior = behavior,
        mode = mode,
        block = block,
        inline = inline,
    )
}

public fun WithDomNode<Element>.scrollTo(
    behavior: org.w3c.dom.ScrollBehavior? = org.w3c.dom.ScrollBehavior.SMOOTH,
    left: Number? = undefined,
    top: Number? = undefined,
) {
    domNode.scrollTo(
        behavior = behavior,
        left = left,
        top = top,
    )
}

public fun Node.scrollIntoView(
    behavior: ScrollBehavior? = ScrollBehavior.smooth,
    mode: ScrollMode? = ScrollMode.always,
    block: ScrollPosition? = ScrollPosition.center,
    inline: ScrollPosition? = ScrollPosition.center,
) {
    scrollIntoView(
        this,
        ScrollIntoViewOptionsInit(
            behavior = behavior,
            mode = mode,
            block = block,
            inline = inline,
        )
    )
}

public fun Element.scrollTo(
    behavior: org.w3c.dom.ScrollBehavior? = org.w3c.dom.ScrollBehavior.SMOOTH,
    left: Number? = undefined,
    top: Number? = undefined,
) {
    scrollTo(
        ScrollToOptions(
            behavior = behavior,
            left = left?.toDouble(),
            top = top?.toDouble(),
        )
    )
}
