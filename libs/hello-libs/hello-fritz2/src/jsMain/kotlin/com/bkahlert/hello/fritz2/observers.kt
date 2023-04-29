package com.bkahlert.hello.fritz2

import dev.fritz2.core.WithDomNode
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.MutationRecord

/** Flow of [MutationRecord], that emits every time DOM mutations take place. */
public val WithDomNode<Element>.observedMutations: Flow<Array<MutationRecord>>
    get() = observedMutations(MutationObserverInit())

/** Flow of [MutationRecord], that emits every time any of the specified [mutations]. */
public fun WithDomNode<Element>.observedMutations(mutations: MutationObserverInit): Flow<Array<MutationRecord>> =
    callbackFlow {
        val observer = MutationObserver { records, _ ->
            trySend(records)
                .onFailure { ex -> console.warn("Failed to observe mutations", records, ex) }
        }

        observer.observe(domNode, mutations)

        awaitClose {
            observer.disconnect()
        }
    }

/** Flow of [ResizeObserverEntry], that emits every time the [Element] is resized. */
public val WithDomNode<Element>.observedResizes: Flow<ResizeObserverEntry>
    get() = callbackFlow {
        val observer = ResizeObserver { entries, _ ->
            trySend(entries.first())
                .onFailure { ex -> console.warn("Failed to observe resize", entries, ex) }
        }

        observer.observe(domNode)

        awaitClose {
            observer.disconnect()
        }
    }

public external class ResizeObserver(callback: (entries: Array<ResizeObserverEntry>, observer: ResizeObserver) -> Unit) {
    public fun disconnect()
    public fun observe(target: Element, options: ResizeObserverOptions? = definedExternally)
    public fun unobserve(target: Element)
}

public external interface ResizeObserverEntry {
    public val borderBoxSize: Array<out ResizeObserverSize>;
    public val contentBoxSize: Array<out ResizeObserverSize>;
    public val contentRect: DOMRectReadOnly;
    public val devicePixelContentBoxSize: Array<out ResizeObserverSize>;
    public val target: Element;
}

public external interface ResizeObserverSize {
    public val blockSize: Double
    public val inlineSize: Double
}

public external interface ResizeObserverOptions {
    /** "border-box" | "content-box" | "device-pixel-content-box" */
    public val box: String?
}
