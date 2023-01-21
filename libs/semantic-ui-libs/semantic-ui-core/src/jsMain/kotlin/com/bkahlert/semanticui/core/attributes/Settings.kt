package com.bkahlert.semanticui.core.attributes

import com.bkahlert.semanticui.core.dom.SemanticElement
import org.w3c.dom.Element
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KProperty

public interface Setting<V> {
    public val name: String

    public companion object {
        public operator fun <V> invoke(name: String): Setting<V> = object : Setting<V> {
            override val name: String = name
        }

        public operator fun <V> invoke(): PropertyDelegateProvider<Nothing?, Setting<V>> =
            PropertyDelegateProvider { _, property -> invoke(property.name) }
    }
}

public inline operator fun <TSemantic : SemanticElement<Element>, reified V> Setting<V>.getValue(
    thisRef: BehaviorScope<TSemantic>,
    property: KProperty<*>
): V? = thisRef.settings[name]?.let { it as? V }

public inline operator fun <TSemantic : SemanticElement<Element>, reified V> Setting<V>.setValue(
    thisRef: BehaviorScope<TSemantic>,
    property: KProperty<*>,
    value: V?
) {
    thisRef.settings[name] = value
}
