package com.bkahlert.hello.components.headlessui

import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.kommons.uri.DataUri
import kotlin.reflect.KProperty1

@Deprecated("remove")
public data class Item(
    val name: String,
    val icon: KProperty1<HeroIcons, DataUri>,
    val items: List<List<Item>>,
    val disabled: Boolean,
) {
    public constructor(
        name: String,
        icon: KProperty1<HeroIcons, DataUri>,
        vararg items: List<Item>,
        disabled: Boolean = false,
    ) : this(name, icon, items.asList(), disabled)
}
