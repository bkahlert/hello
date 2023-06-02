package com.bkahlert.hello.components.headlessui

import com.bkahlert.hello.components.navigationbar.SimpleNavItem
import com.bkahlert.hello.components.navigationbar.SimpleNavItemGroup
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.kommons.uri.Uri

@Deprecated("remove")
public data class ItemNavItem(private val item: Item) : SimpleNavItem<Item>(item) {
    override val label: String get() = value.name
    override val icon: Uri get() = value.icon.get(OutlineHeroIcons)
    override val activeIcon: Uri get() = value.icon.get(SolidHeroIcons)
    override val disabled: Boolean get() = value.disabled
    override val groups: List<SimpleNavItemGroup<Item>> = value.items.map { list -> list.map { ItemNavItem(it) } }
}
