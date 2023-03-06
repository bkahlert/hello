package com.bkahlert.semanticui.module

import org.w3c.dom.Element

public external interface SemanticPopup : SemanticModule {
    public fun popup(behavior: String, vararg args: Any?): dynamic
}

public fun Element.popup(settings: SemanticModuleSettingsBuilder<SemanticPopupSettings>): SemanticPopup = SemanticUI.create(this, "popup", settings.build())


public external interface SemanticPopupSettings : SemanticModuleSettings {
    /**
     * Position that the popup should appear
     */
    public var position: String?

    /**
     * When set to false, a popup will not appear and produce an error message if it cannot entirely fit on page. Setting this to a position like, right center forces the popup to use this position as a last resort even if it is partially offstage. Setting this to true will use the last attempted position.
     */
    public var lastResort: Boolean?

    /**
     * Popup variation to use, can use multiple variations with a space delimiter
     */
    public var variation: String?

    /**
     * Content to display
     */
    public var content: String?

    /**
     * Title to display alongside content
     */
    public var title: String?

    /**
     * HTML content to display instead of preformatted title and content
     */
    public var html: String?
}

// @formatter:off
/** Shows the popup */
public inline fun SemanticPopup.show(noinline callback: (() -> Unit)? = null): SemanticPopup = popup("show", callback).unsafeCast<SemanticPopup>()
/** Hides the popup */
public inline fun SemanticPopup.hide(noinline callback: (() -> Unit)? = null): SemanticPopup = popup("hide", callback).unsafeCast<SemanticPopup>()

public inline fun SemanticPopup.destroy(): dynamic = popup("destroy")
// @formatter:on
