package playground.components.bookmark

import com.bkahlert.hello.button.button
import com.bkahlert.hello.editor.selectField
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.metadata.fetchFavicon
import com.bkahlert.hello.widget.AspectRatio
import com.bkahlert.hello.widget.WidgetEditor
import com.bkahlert.kommons.dom.mapTarget
import com.bkahlert.kommons.dom.readText
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.json.LenientAndPrettyJson
import com.bkahlert.kommons.uri.Uri
import dev.fritz2.core.EmittingHandler
import dev.fritz2.core.Handler
import dev.fritz2.core.RenderContext
import dev.fritz2.core.accept
import dev.fritz2.core.lensOf
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import dev.fritz2.headless.components.textArea
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.get

public class BookmarksWidgetEditor(isNew: Boolean, widget: BookmarksWidget) : WidgetEditor<BookmarksWidget>(isNew, widget) {

    private val logger by ConsoleLogging(namespace = "hello")

    public val import: EmittingHandler<File, Throwable> = handleAndEmit { current, file ->
        logger.info("Importing ${file.name}")
        BookmarkFormat.runCatching {
            current.copy(bookmarks = parse(file.readText()))
        }.getOrElse {
            logger.error("Failed to import ${file.name}", it)
            emit(it)
            current
        }
    }

    public val fixMissingIcons: Handler<Unit> = handle { current, file ->
        logger.info("Fixing missing icons")
        val x = mutableMapOf<BookmarkTreeNode.Bookmark, Uri?>()
        current.bookmarks.mapBookmarks { bookmark ->
            bookmark.also { x.put(it, null) }
        }

        // TODO use tracker
        coroutineScope {
            x.keys.forEach { bookmark ->
                launch {
                    x.put(bookmark, bookmark.uri?.fetchFavicon())
                }
            }
        }

        current.copy(
            bookmarks = current.bookmarks.mapBookmarks { bookmark ->
                x.get(bookmark)?.let { bookmark.copy(icon = it) } ?: bookmark
            }
        )
    }

    override fun RenderContext.renderFields() {
        div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),1fr))] gap-4 items-start") {
            button(
                OutlineHeroIcons.cloud_arrow_up,
                "Import bookmarks",
                "Restore your bookmarks by uploading a previously exported copy.",
                simple = true,
                inverted = true
            ).apply {
                className("relative")
                input("absolute inset-0 w-full h-full p-0 m-0 outline-none opacity-0 cursor-pointer") {
                    accept("application/html")
                    type("file")
                    dragovers.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.add("animate-bounce") }
                    dragleaves.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.remove("animate-bounce") }
                    dragends.mapTarget<HTMLInputElement>() handledBy { it.parentElement?.querySelector("svg")?.classList?.remove("animate-bounce") }
                    changes.mapTarget<HTMLInputElement>().mapNotNull { it.files?.get(0) } handledBy import
                }
            }
            button(
                OutlineHeroIcons.cloud_arrow_down,
                "Export bookmarks",
                "Make a backup of your current bookmarks by downloading a copy.",
                simple = true,
                inverted = true
            ).apply {
                clicks handledBy { console.warn("export bookmarks") }
            }
        }
        inputField {
            val store = map(BookmarksWidget.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("My favorite bookmarks")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        textArea {
            val store = map(
                BookmarksWidget.bookmarks().plus(
                    lensOf(
                        id = "json",
                        getter = { v -> LenientAndPrettyJson.encodeToString(v) },
                        setter = { _, v -> LenientAndPrettyJson.decodeFromString(v) })
                )
            )
            value(store)
            textareaLabel {
                +"JSON"
                textareaTextfield("h-40") {
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        selectField(
            store = map(BookmarksWidget.style()),
            label = "Style",
            itemTitle = BookmarksStyle::title,
        )
        div("grid grid-cols-[repeat(auto-fit,_minmax(min(20rem,_100%),1fr))] gap-4 items-start") {
            button(
                OutlineHeroIcons.wrench,
                "Fix missing icons",
                simple = true,
                inverted = true
            ).apply {
                clicks handledBy fixMissingIcons
            }
        }

        selectField(
            store = map(BookmarksWidget.aspectRatio()),
            label = "Aspect ratio",
            itemTitle = AspectRatio::title,
            itemIcon = AspectRatio::icon,
        )
    }
}
