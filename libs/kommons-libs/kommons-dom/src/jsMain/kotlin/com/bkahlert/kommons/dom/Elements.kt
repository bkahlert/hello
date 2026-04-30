package com.bkahlert.kommons.dom

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.ElementCreationOptions
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.MutationRecord

/**
 * A number in the range [0..1], that describes what percentage of the element's scroll height can be displayed.
 * A value of `1` signifies that no vertical scrolling is needed.
 */
public val Element.verticalScrollCoverageRatio: Double
    get() = (clientHeight / scrollHeight.toDouble()).coerceIn(0.0, 1.0)

/** A number in the range [0..1], that describes to what percentage the element is scrolled to the bottom. */
public val Element.verticalScrollProgress: Double
    get() = (scrollTop / (scrollHeight - clientHeight).toDouble()).coerceIn(0.0, 1.0)


/** Flow of [MutationRecord], that emits every time DOM mutations take place. */
public val Element.observedMutations: Flow<Array<MutationRecord>>
    get() = observedMutations(MutationObserverInit())

/** Flow of [MutationRecord], that emits every time any of the specified [mutations]. */
public fun Element.observedMutations(mutations: MutationObserverInit): Flow<Array<MutationRecord>> =
    callbackFlow {
        val observer = MutationObserver { records, _ ->
            trySend(records)
                .onFailure { ex -> console.warn("Failed to observe mutations", records, ex) }
        }

        observer.observe(this@observedMutations, mutations)

        awaitClose {
            observer.disconnect()
        }
    }

/** Flow of [ResizeObserverEntry], that emits every time the [Element] is resized. */
public val Element.observedResizes: Flow<ResizeObserverEntry>
    get() = callbackFlow {
        val observer = ResizeObserver { entries, x ->
            trySend(entries.first())
                .onFailure { ex -> console.warn("Failed to observe resize", entries, ex) }
        }

        observer.observe(this@observedResizes)

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


/**
 * [data] gets arbitrary `data` attribute of the Element.
 */
public fun Element.data(dataAttr: String): String? = getAttribute("data-$dataAttr")

/**
 * [data] adds arbitrary `data` attribute to the Element.
 */
public fun Element.data(dataAttr: String, value: String?) {
    val qualifiedName = "data-$dataAttr"
    when (value) {
        null -> removeAttribute(qualifiedName)
        else -> setAttribute(qualifiedName, value)
    }
}

/**
 * Adds each of the given [cssClasses]
 * if this element does not have the respective CSS class style in its 'class' attribute
 * and removes it otherwise.
 *
 * @return `true` if at least one class has been toggled
 */
public fun Element.toggleClass(vararg cssClasses: String): Boolean = cssClasses.map { cssClass ->
    if (hasClass(cssClass)) removeClass(cssClass)
    else addClass(cssClass)
}.any { it }

/**
 * Returns an [Element]
 * - created using the specified [localName] and the optional [options],
 * - with the specified [block] applied to it, and
 * - appended to the current element as a new child.
 */
public inline fun <reified T : Element> Element.appendTypedElement(
    localName: String,
    options: ElementCreationOptions? = null,
    block: T.() -> Unit = {},
): T {
    val document: Document = requiredOwnerDocument
    val createdElement: T = if (options != null) {
        document.createElement(localName, options) as T
    } else {
        document.createElement(localName) as T
    }
    createdElement.apply(block)
    append(createdElement)
    return createdElement
}

/**
 * Returns an [HTMLDivElement]
 * - created using the optional [options],
 * - with the specified [block] applied to it, and
 * - appended to the current element as a new child.
 */
public fun Element.appendDivElement(
    options: ElementCreationOptions? = null,
    block: HTMLDivElement.() -> Unit = {},
): HTMLDivElement = appendTypedElement<HTMLDivElement>("div", options, block)
