package com.bkahlert.semanticui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.core.dom.SemanticContentBuilder
import com.bkahlert.semanticui.core.dom.SemanticElement
import com.bkahlert.semanticui.core.jQuery
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Segment
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement

@Stable
public interface DemoViewState {
    public val active: String?
    public fun onActivate(id: String)
}

@Composable
public fun rememberDemoViewState(
    active: String? = null,
): DemoViewState = remember(active) {
    object : DemoViewState {
        override var active: String? by mutableStateOf(active)
        override fun onActivate(id: String) {
            this.active = id
        }
    }
}

/** A composable to view all specified [providers]. */
@Composable
public fun DemoView(
    vararg providers: DemoProvider,
    state: DemoViewState = rememberDemoViewState(),
    trashContent: DemoContentBuilder? = null,
) {
    val tabs: Set<DemoProviderTab> = remember(trashContent, providers) {
        buildSet {
            if (trashContent != null) add(
                DemoProviderTab(DemoProvider("trash", "—", null, trashContent)) {
                    Icon("teal", "colored", "trash")
                }
            )
            providers.mapTo(this) {
                DemoProviderTab(it) {
                    when (val src = it.logo) {
                        null -> Text(it.name)
                        else -> {
                            Img(
                                src = src.toString(),
                                alt = it.name
                            ) {
                                style {
                                    width(1.5.em)
                                    marginRight(0.5.em)
                                }
                            }
                            Text(it.name)
                        }
                    }
                }
            }
        }.apply {
            console.info("DemoView tabs: ${map { it.id }}")
        }

    }
    val activeTab by derivedStateOf {
        state.active?.let { tabs.firstOrNull { tab -> tab.id == it } }.apply {
            console.info("DemoView active tab: ${this?.id}")
        }
    }

    TabMenu(
        tabs = tabs,
        activeTab = activeTab,
        firstContent = {
            S("header", "item") { Text("Demos") }
        },
        onActivate = { tab ->
            console.info("DemoView tab activation: $tab")
            if (tab is DemoProviderTab) {
                state.onActivate(tab.id)
            }
        },
    )
}

private class DemoProviderTab(
    val provider: DemoProvider,
    override val tabAttrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    override val tabContent: ContentBuilder<HTMLAnchorElement>,
) : Tab {
    override val id: String get() = provider.id
    override val content: SemanticContentBuilder<SemanticElement<HTMLDivElement>> get() = provider.content
    override fun toString(): String = "${DemoProviderTab::class.simpleName}($id)"
}

private interface Tab {
    val id: String
    val tabAttrs: AttrBuilderContext<HTMLAnchorElement>?
    val tabContent: ContentBuilder<HTMLAnchorElement>
    val content: SemanticContentBuilder<SemanticElement<HTMLDivElement>>
}

@Composable
private fun TabMenu(
    tabs: Set<Tab> = emptySet(),
    activeTab: Tab? = null,
    firstContent: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
    lastContent: SemanticContentBuilder<SemanticElement<HTMLDivElement>>? = null,
    onActivate: (Tab) -> Unit = { tab -> console.info("onActivate($tab)") },
) {
    val currentTab = activeTab ?: tabs.firstOrNull()
    S("ui", "pointing", "stackable", "icon", "menu") {
        firstContent?.invoke(this)
        tabs.forEach { tab ->
            A(null, {
                tab.tabAttrs?.invoke(this)
                classes("item")
                if (tab == currentTab) classes("active")
                onClick { onActivate(tab) }
            }, tab.tabContent)
        }
        lastContent?.invoke(this)
    }
    Segment {
        when (currentTab) {
            null -> {
                Text("—")
            }

            else -> {
                currentTab.content(this)
                DisposableEffect(currentTab) {
                    jQuery(".modal").modal("refresh")
                    onDispose { }
                }
            }
        }
    }
}
