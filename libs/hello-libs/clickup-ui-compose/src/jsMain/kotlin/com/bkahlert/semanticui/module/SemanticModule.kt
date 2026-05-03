package com.bkahlert.semanticui.module

import androidx.compose.runtime.Composable
import com.bkahlert.kommons.js.ConsoleLogger
import com.bkahlert.kommons.js.grouping
import com.bkahlert.kommons.js.table
import com.bkahlert.semanticui.core.attributes.SemanticAttrsScope
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import js.core.ArrayLike
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

public external interface SemanticModule
public external interface SemanticModuleSettings : DebugSettings
public typealias SemanticModuleSettingsBuilder<T> = T.() -> Unit

/**
 * Returns settings with using this builder applied
 * to either a new settings instance or the specified [settings] instance.
 */
internal fun <T : SemanticModuleSettings> SemanticModuleSettingsBuilder<T>.build(
    settings: T = js("{}") as T,
): T = settings.apply(this)

private val logger = ConsoleLogger("Semantic UI")

@JsName("SemanticUI")
public external object SemanticUI {

    @JsName("jQuery")
    public val jQuery: dynamic

    /** Creates a jQuery instance using the optional [deep]. */
    @JsName("jQuery")
    public fun jQuery(deep: String): JQuery<HTMLElement>

    /** Creates a jQuery instance using the optional [element]. */
    @JsName("jQuery")
    public fun <TElement : HTMLElement> jQuery(element: HTMLElement): JQuery<TElement>

    /** Creates a jQuery instance using the optional [element]. */
    @JsName("jQuery")
    public fun jQuery(element: Element): JQuery<HTMLElement>

    /** Creates a jQuery instance using the optional [elements]. */
    @JsName("jQuery")
    public fun <TElement : HTMLElement> jQuery(elements: ArrayLike<Element>): JQuery<TElement>

    @JsName("semanticModules")
    public val modules: Array<String>

    @JsName("semanticConstructor")
    public fun <T : SemanticModule> create(
        element: Element,
        module: String,
        settings: SemanticModuleSettings = definedExternally,
    ): T
}

public fun updateDebugSettings(configure: (String, DebugSettings) -> Unit) {
    logger.grouping(::updateDebugSettings) {
        val updatedSettings = SemanticUI.modules.map { module ->
            val settings: SemanticModuleSettings = SemanticUI.jQuery.fn[module].settings.unsafeCast<SemanticModuleSettings>()
            configure(module, settings)
            settings
        }
        logger.table(
            updatedSettings,
            DebugSettings::debug,
            DebugSettings::performance,
            DebugSettings::verbose,
        ) { it.name }
    }
}

public external interface DebugSettings {
    /** Name used in debug logs */
    public var name: String

    /**
     * Silences all console output including error messages, regardless of other debug settings.
     */
    public var silent: Boolean?

    /** Provides standard debug output to console */
    public var debug: Boolean?

    /** Provides standard debug output to console */
    public var performance: Boolean?

    /** Provides ancillary debug output to console */
    public var verbose: Boolean?
}


/**
 * Creates a [SemanticModuleElement] representing element with
 * the specified [semanticModuleAttrs], and
 * the specified [semanticModuleContent]
 * based on a [HTMLDivElement].
 */
@Composable
public fun <TSemantic : SemanticElement<HTMLElement>, TSettings : SemanticModuleSettings> SemanticModuleElement(
    semanticModuleAttrs: SemanticModuleAttrBuilderContext<TSemantic, TSettings>? = null,
    semanticModuleContent: SemanticModuleContentBuilder<TSemantic, TSettings>? = null,
): Unit {
    val settingsAcc: TSettings = (fun TSettings.(): Unit {}).build()
    SemanticElement(
        semanticAttrs = moduleAttrsContext(semanticModuleAttrs, settingsAcc),
        semanticContent = moduleContentContext(semanticModuleContent, settingsAcc),
    ) { attrs, content -> Div(attrs, content) }
}


public interface SemanticModuleAttrsScope<out TSemantic : SemanticElement<Element>, TSettings : SemanticModuleSettings> : SemanticAttrsScope<TSemantic> {
    public fun settings(settings: SemanticModuleSettingsBuilder<TSettings>)
}

public interface SemanticModuleElementScope<out TSemantic : SemanticElement<Element>, TSettings : SemanticModuleSettings> : SemanticElementScope<TSemantic> {
    public val settings: TSettings
}

public typealias SemanticModuleAttrBuilderContext<T, TSettings> = SemanticModuleAttrsScope<T, TSettings>.() -> Unit
public typealias SemanticModuleContentBuilder<T, TSettings> = @Composable SemanticModuleElementScope<T, TSettings>.() -> Unit


internal fun <TElement : HTMLElement, TSemantic : SemanticElement<TElement>, TSettings : SemanticModuleSettings> moduleAttrsContext(
    moduleAttrs: SemanticModuleAttrBuilderContext<TSemantic, TSettings>?,
    settingsAcc: TSettings,
): SemanticAttrBuilderContext<TSemantic>? {
    if (moduleAttrs == null) return null
    return { SemanticModuleAttrsScopeBuilder(this, settingsAcc).moduleAttrs() }
}

internal class SemanticModuleAttrsScopeBuilder<
    TSemantic : SemanticElement<HTMLElement>,
    TSettings : SemanticModuleSettings,
    >(
    semanticAttrsScope: SemanticAttrsScope<TSemantic>,
    private val settingsAcc: TSettings,
) : SemanticModuleAttrsScope<TSemantic, TSettings>, SemanticAttrsScope<TSemantic> by semanticAttrsScope {
    override fun settings(settings: SemanticModuleSettingsBuilder<TSettings>) {
        settings.invoke(settingsAcc)
    }
}


internal fun <TElement : HTMLElement, TSemantic : SemanticElement<TElement>, TSettings : SemanticModuleSettings> moduleContentContext(
    moduleContent: SemanticModuleContentBuilder<TSemantic, TSettings>?,
    settingsAcc: TSettings,
): SemanticContentBuilder<TSemantic>? {
    if (moduleContent == null) return null
    return { SemanticModuleElementScopeBuilder(this, settingsAcc).moduleContent() }
}

internal class SemanticModuleElementScopeBuilder<
    TSemantic : SemanticElement<HTMLElement>,
    TSettings : SemanticModuleSettings,
    >(
    semanticElementScope: SemanticElementScope<TSemantic>,
    private val settingsAcc: TSettings,
) : SemanticModuleElementScope<TSemantic, TSettings>, SemanticElementScope<TSemantic> by semanticElementScope {
    override val settings: TSettings
        get() = settingsAcc
}
