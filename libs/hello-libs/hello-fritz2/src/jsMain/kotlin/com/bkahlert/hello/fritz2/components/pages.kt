package com.bkahlert.hello.fritz2.components

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.OutlineHeroIcons
import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.components.navigationbar.NavItem
import com.bkahlert.hello.fritz2.components.navigationbar.SimpleNavItem
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import kotlin.reflect.KProperty1

public interface HasTitle {
    public val label: String
}

public interface HasIcon {
    public val icon: Uri
    public val activeIcon: Uri get() = icon
}

public interface HasPageContent {
    public val pageContent: ContentBuilder?
}

public open class Page(
    public val id: String,
    override val label: String,
    description: String? = null,
    override val icon: Uri,
    override val activeIcon: Uri = icon,
    disabled: Boolean = false,
    public val groups: List<List<HasPageContent>> = emptyList(),
    override val pageContent: ContentBuilder? = null,
) : HasTitle, HasIcon, HasPageContent, NavItem by SimpleNavItem(label, description, icon, activeIcon, disabled, buildList<List<SimpleNavItem>> {
    groups.forEach { group: List<HasPageContent> ->
        add(group.filterIsInstance<SimpleNavItem>())
    }
}) {

    public constructor(
        id: String,
        label: String,
        description: String?,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: List<HasPageContent>,
        disabled: Boolean = false,
        pageContent: ContentBuilder? = null,
    ) : this(
        id = id,
        label = label,
        description = description,
        icon = heroIcon.get(OutlineHeroIcons),
        activeIcon = heroIcon.get(SolidHeroIcons),
        disabled = disabled,
        groups = groups.asList(),
        pageContent = pageContent,
    )

    public constructor(
        id: String,
        label: String,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: List<HasPageContent>,
        disabled: Boolean = false,
        pageContent: ContentBuilder? = null,
    ) : this(id, label, null, heroIcon, *groups, disabled = disabled, pageContent = pageContent)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Page

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

public val Page.pages: List<Page>
    get() = groups.flatten().filterIsInstance<Page>()
