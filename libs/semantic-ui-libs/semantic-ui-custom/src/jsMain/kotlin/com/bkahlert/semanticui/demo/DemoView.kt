package com.bkahlert.semanticui.demo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.bkahlert.kommons.dom.LocationFragmentParameters
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.semanticui.core.S
import com.bkahlert.semanticui.custom.Tab
import com.bkahlert.semanticui.custom.TabMenu
import com.bkahlert.semanticui.custom.rememberReportingCoroutineScope
import com.bkahlert.semanticui.custom.rememberTabMenuState
import com.bkahlert.semanticui.element.Icon
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLAnchorElement

private val logger by ConsoleLogging("DemoView")

public interface DemoViewState {
    public val activeId: String?
    public fun onActivate(id: String?)
}

// effectively holds the state in the mutable [activeId]
@Composable
public fun rememberDemoViewState(
    activeId: String?,
    onActivate: ((String?) -> Unit)? = null, // optional callback
): DemoViewState = remember(activeId, onActivate) {
    object : DemoViewState {
        override var activeId: String? by mutableStateOf(activeId)
        override fun onActivate(id: String?) {
            this.activeId = id
            onActivate?.invoke(id)
        }
    }
}

// effectively holds the state in the fragment parameter
@Composable
public fun LocationFragmentParameters.asDemoViewState(
    name: String,
    onActivate: ((String?) -> Unit)? = null, // optional callback
): DemoViewState {
    val state: State<String?> = asFlow().map { it[name] }.collectAsState(get(name))
    return remember(this, name, state) {
        object : DemoViewState {
            override val activeId: String?
                get() = state.value

            override fun onActivate(id: String?) {
                set(name, id)
                onActivate?.invoke(id)
            }
        }
    }
}

/** A composable to view all specified [providers]. */
@Composable
public fun DemoView(
    vararg providers: DemoProvider,
    state: DemoViewState = rememberDemoViewState(providers.firstOrNull()?.id),
    trashContent: DemoContentBuilder? = null,
) {
    val tabs = remember {
        buildSet {
            if (trashContent != null) {
                add(DemoProviderTab(DemoProvider("trash", "—", null, trashContent)) {
                    Icon("teal", "colored", "trash")
                })
            }

            providers
                .filterNot { it.content.isEmpty() }
                .forEach {
                    add(DemoProviderTab(it) {
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
                    })
                }
        }
    }

    val tabMenuState = rememberTabMenuState(tabs, state.activeId)

    TabMenu(
        state = tabMenuState,
        attrs = { classes("pointing", "stackable", "icon") },
        firstContent = {
            S("header", "item") { Text("Demos") }
        },
    )

    LaunchedEffect(tabMenuState) {
        snapshotFlow { tabMenuState.active }
            .collect { tab ->
                logger.debug("activate", tab, "state.active", state.activeId)
                state.onActivate(tab)
            }
    }
}

private class DemoProviderTab(
    val provider: DemoProvider,
    override val tabAttrs: AttrBuilderContext<HTMLAnchorElement>? = null,
    override val tabContent: ContentBuilder<HTMLAnchorElement>,
) : Tab {
    override val id: String get() = provider.id
    override val content: @Composable () -> Unit = @Composable {
        S(
            "ui",
            provider.content.size.orZero().coerceIn(1..3).toWord(),
            "column", "stackable", "doubling", "grid", "segment",
        ) {
            val demoProviderScope = rememberReportingCoroutineScope()
            provider.content.forEach { demoProviderContent ->
                S("column", content = { demoProviderContent(demoProviderScope) })
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false
        other as DemoProviderTab
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "${DemoProviderTab::class.simpleName}($id)"
}


private fun Int?.orZero() = this ?: 0

private fun Int.toWord(
    lessThanOneValue: String = "one",
    greaterThanSixteenValue: String = "sixteen",
) = when (this) {
    in Int.MIN_VALUE until 1 -> lessThanOneValue
    1 -> "one"
    2 -> "two"
    3 -> "three"
    4 -> "four"
    5 -> "five"
    6 -> "six"
    7 -> "seven"
    8 -> "eight"
    9 -> "nine"
    10 -> "ten"
    11 -> "eleven"
    12 -> "twelve"
    13 -> "thirteen"
    14 -> "fourteen"
    15 -> "fifteen"
    16 -> "sixteen"
    else -> greaterThanSixteenValue
}
