package com.bkahlert.hello.components

import com.bkahlert.hello.components.navigationbar.SimpleNavItem
import com.bkahlert.hello.components.navigationbar.SimpleNavItemGroup
import com.bkahlert.hello.page.Page
import com.bkahlert.hello.page.ParentPage
import com.bkahlert.kommons.uri.Uri

public class PageNavItem(page: Page) : SimpleNavItem<Page>(page) {
    override val label: String get() = value.name
    override val icon: Uri get() = value.icon
    override val activeIcon: Uri get() = value.activeIcon
    override val disabled: Boolean get() = false
    override val groups: List<SimpleNavItemGroup<Page>> = listOf(
        value.let { it as? ParentPage }?.pages?.map { PageNavItem(it) } ?: emptyList()
    )
}

public fun Page.asNavItem(): PageNavItem = PageNavItem(this)
public fun List<Page>.toNavItems(): List<PageNavItem> = map { it.asNavItem() }
