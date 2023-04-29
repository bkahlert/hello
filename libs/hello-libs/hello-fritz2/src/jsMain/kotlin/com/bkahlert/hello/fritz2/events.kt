package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.dom.verticalScrollCoverageRatio
import com.bkahlert.kommons.dom.verticalScrollProgress
import dev.fritz2.core.WithDomNode
import dev.fritz2.core.WithEvents
import dev.fritz2.core.subscribe
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.w3c.dom.Element
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.Window
import org.w3c.dom.events.Event

/** Flow of [verticalScrollCoverageRatio] as it changes over time. */
public val WithDomNode<Element>.verticalScrollCoverageRatios: Flow<Double>
    get() = merge(
        window.subscribe<Event, Window>("resize")
            .conflate().map { domNode.verticalScrollCoverageRatio },
        observedMutations(MutationObserverInit(childList = true, subtree = true, attributes = true, characterData = true))
            .conflate().map { domNode.verticalScrollCoverageRatio }
    )

/** Flow of [verticalScrollProgress] as it changes over time. */
public val WithEvents<Element>.verticalScrollProgresses: Flow<Double>
    get() = scrolls.conflate().mapTarget<Element>().map { it.verticalScrollProgress }

/** Flow of [Element.scrollTop] as it changes over time. */
public val WithEvents<Element>.scrollTops: Flow<Double>
    get() = scrolls.conflate().mapTarget<Element>().map { it.scrollTop }
