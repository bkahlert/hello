@file:Suppress("RedundantVisibilityModifier")

package playground.components.bookmark

import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.Widget
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.hello.widget.panel
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement

@Serializable
public data class BookmarksWidget(
    override val id: String,
    override val title: String? = null,
    @SerialName("bookmarks") val bookmarks: List<BookmarkTreeNode> = emptyList(),
    @SerialName("style") val style: BookmarksStyle? = null,
    @SerialName("aspect-ratio") val aspectRatio: AspectRatio? = AspectRatio.stretch,
) : Widget {

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(aspectRatio) {
        with(style ?: BookmarksStyle.ListStyle) {
            render(bookmarks)
        }
    }

    override fun editor(isNew: Boolean): WidgetEditor<*> = BookmarksWidgetEditor(isNew, this)

    public companion object {
        public fun title(): Lens<BookmarksWidget, String> =
            BookmarksWidget::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun bookmarks(): Lens<BookmarksWidget, List<BookmarkTreeNode>> =
            BookmarksWidget::bookmarks.lens({ it.bookmarks }, { p, v -> p.copy(bookmarks = v) })

        public fun style(): Lens<BookmarksWidget, BookmarksStyle?> =
            BookmarksWidget::style.lens({ it.style }, { p, v -> p.copy(style = v) })

        public fun aspectRatio(): Lens<BookmarksWidget, AspectRatio?> =
            BookmarksWidget::aspectRatio.lens({ it.aspectRatio }, { p, v -> p.copy(aspectRatio = v) })
    }
}
