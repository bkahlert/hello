package com.bkahlert.semanticui.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.MenuElement
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement

@Composable
public fun <T : Tab> TabMenu(
    state: TabMenuState<T> = rememberTabMenuState(),
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
    firstContent: SemanticContentBuilder<MenuElement>? = null,
    lastContent: SemanticContentBuilder<MenuElement>? = null,
) {
    Menu({
        attrs?.invoke(this)
    }) {
        firstContent?.invoke(this)
        state.tabs.forEach { tab ->
            key(tab.id) {
                A(null, {
                    tab.tabAttrs?.invoke(this)
                    classes("item")
                    if (tab.id == state.active) classes("active")
                    onClick { state.active = tab.id }
                }, tab.tabContent)
            }
        }
        lastContent?.invoke(this)
    }

    state.activeTab?.content?.invoke()
}

@Stable
public interface Tab {
    public val id: String
    public val tabAttrs: AttrBuilderContext<HTMLAnchorElement>?
    public val tabContent: ContentBuilder<HTMLAnchorElement>
    public val content: @Composable () -> Unit
}

public class SimpleTab(
    override val id: String,
    override val content: @Composable () -> Unit
) : Tab {
    override val tabAttrs: AttrBuilderContext<HTMLAnchorElement>? get() = null
    override val tabContent: ContentBuilder<HTMLAnchorElement> get() = { Text(id) }
}

@Stable
public class TabMenuState<T : Tab>(
    tabs: Collection<T>,
    active: String?,
) {
    public val tabs: MutableList<T> = mutableStateListOf<T>().apply { addAll(tabs) }
    public var active: String? by mutableStateOf(active)
    public val activeTab: T? get() = tabs.firstOrNull { it.id == active }
}

@Composable
public fun <T : Tab> rememberTabMenuState(
    tabs: Collection<T> = emptyList(),
    active: String? = null,
): TabMenuState<T> = remember(tabs, active) {
    TabMenuState(tabs, active)
}
