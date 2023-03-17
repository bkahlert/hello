@file:Suppress("RedundantVisibilityModifier")

package playground.components

import dev.fritz2.core.Id
import dev.fritz2.core.RenderContext
import dev.fritz2.core.ScopeContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.headless.foundation.Aria
import dev.fritz2.headless.foundation.TagFactory
import dev.fritz2.headless.foundation.addComponentStructureInfo
import dev.fritz2.headless.foundation.attrIfNotSet
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
public annotation class ScreensDsl

@ScreensDsl
class Screens<C : HTMLElement>(tag: Tag<C>, id: String?) : Tag<C> by tag {

    val componentId: String by lazy { id ?: Id.next() }
    private var screenCount = 0

    @ScreensDsl
    class Screen<C : HTMLElement, CS : HTMLElement>(
        private val screens: Screens<C>,
        tag: Tag<CS>,
        val index: Int
    ) : Tag<CS> by tag {
        fun render() {
            attrIfNotSet("role", Aria.Role.main)
        }
    }

    fun <CS : HTMLElement> RenderContext.screen(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        tag: TagFactory<Tag<CS>>,
        initialize: Screen<C, CS>.() -> Unit
    ) {
        val index = screenCount++
        addComponentStructureInfo("screen", this@screen.scope, this)
        tag(this, classes, "$componentId-screen-$index", scope) {
            Screen(this@Screens, this, index).run {
                initialize()
                render()
            }
        }
    }

    fun RenderContext.screen(
        classes: String? = null,
        scope: (ScopeContext.() -> Unit) = {},
        initialize: Screen<C, HTMLDivElement>.() -> Unit
    ) = screen(classes, scope, RenderContext::div, initialize)
}

fun RenderContext.verticalScreens(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    initialize: Screens<HTMLDivElement>.() -> Unit
): Tag<HTMLDivElement> {
    addComponentStructureInfo("verticalScreens", this@verticalScreens.scope, this)
    return div(classes("snap-v-full", classes), id, scope) {
        Screens(this, id).run {
            initialize()
        }
    }
}

fun RenderContext.horizontalScreens(
    classes: String? = null,
    id: String? = null,
    scope: (ScopeContext.() -> Unit) = {},
    initialize: Screens<HTMLDivElement>.() -> Unit
): Tag<HTMLDivElement> {
    addComponentStructureInfo("horizontalScreens", this@horizontalScreens.scope, this)
    return div(classes("snap-h-full", classes), id, scope) {
        Screens(this, id).run {
            initialize()
        }
    }
}
