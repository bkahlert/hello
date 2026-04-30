package com.bkahlert.hello.fritz2

import com.bkahlert.kommons.dom.ResizeObserverEntry
import com.bkahlert.kommons.dom.observedMutations
import com.bkahlert.kommons.dom.observedResizes
import dev.fritz2.core.WithDomNode
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Element
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.MutationRecord

/** Flow of [MutationRecord], that emits every time DOM mutations take place. */
public val WithDomNode<Element>.observedMutations: Flow<Array<MutationRecord>>
    get() = domNode.observedMutations

/** Flow of [MutationRecord], that emits every time any of the specified [mutations]. */
public fun WithDomNode<Element>.observedMutations(mutations: MutationObserverInit): Flow<Array<MutationRecord>> =
    domNode.observedMutations(mutations)

/** Flow of [ResizeObserverEntry], that emits every time the [Element] is resized. */
public val WithDomNode<Element>.observedResizes: Flow<ResizeObserverEntry>
    get() = domNode.observedResizes
