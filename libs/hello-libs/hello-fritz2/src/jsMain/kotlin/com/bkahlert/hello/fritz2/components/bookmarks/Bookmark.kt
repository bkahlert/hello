package com.bkahlert.hello.fritz2.components.bookmarks

import com.bkahlert.hello.fritz2.UriLens
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.icon
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.classes
import dev.fritz2.core.href
import dev.fritz2.core.title
import dev.fritz2.headless.foundation.TagFactory
import kotlinx.browser.window
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLElement

@Serializable
public data class Bookmark(
    @SerialName("id") val id: String = randomString(),
    @SerialName("title") val title: String = "",
    @SerialName("href") val href: Uri? = null,
    @SerialName("icon") val icon: Uri? = null,
) {
    public fun open() {
        href?.also { window.open(href.toString()) }
    }

    public fun RenderContext.render(
        classes: String? = null,
        content: Tag<HTMLAnchorElement>.() -> Unit = {},
    ): Tag<HTMLAnchorElement> = render(classes, RenderContext::a) {
        href?.toString()?.also { href(it) }
        content()
    }

    public fun <C : HTMLElement> RenderContext.render(
        classes: String? = null,
        tag: TagFactory<Tag<C>>,
        content: Tag<C>.() -> Unit = {},
    ): Tag<C> = tag(this, classes, null, {}) {
        className(
            classes(
                "inline-flex justify-center transition opacity-60 hover:opacity-100",
                "focus:outline-none focus-visible:ring-4 focus-visible:ring-white focus-visible:ring-opacity-75",
                classes,
            )
        )
        title(title)
        icon("w-full h-full", icon ?: SolidHeroIcons.bookmark)
        content()
    }

    public companion object {
        public fun title(): Lens<Bookmark, String> =
            Bookmark::title.lens({ it.title }) { p, v -> p.copy(title = v) }

        public fun uri(): Lens<Bookmark, String> =
            Bookmark::href.lens({ it.href }, { p, v -> p.copy(href = v?.toUriOrNull()) }) + UriLens

        public fun icon(): Lens<Bookmark, String> =
            Bookmark::icon.lens({ it.icon }, { p, v -> p.copy(icon = v?.toUriOrNull()) }) + UriLens
    }
}
