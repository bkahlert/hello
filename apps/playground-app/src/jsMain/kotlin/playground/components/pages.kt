@file:Suppress("RedundantVisibilityModifier")

package playground.components

import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import playground.fritz2.ContentBuilder
import playground.tailwind.heroicons.HeroIcons
import playground.tailwind.heroicons.OutlineHeroIcons
import playground.tailwind.heroicons.SolidHeroIcons
import kotlin.reflect.KProperty1

public open class Page(
    public val id: String,
    public override val label: String,
    public override val description: String? = null,
    public override val icon: Uri,
    public override val activeIcon: Uri = icon,
    public override val disabled: Boolean = false,
    public override val groups: List<PageGroup> = emptyList(),
    public val content: ContentBuilder? = null,
) : NavItem by SimpleNavItem(label, description, icon, activeIcon, disabled, groups) {

    constructor(
        id: String,
        label: String,
        description: String?,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: PageGroup,
        disabled: Boolean = false,
        content: ContentBuilder? = null,
    ) : this(
        id = id,
        label = label,
        description = description,
        icon = heroIcon.get(OutlineHeroIcons),
        activeIcon = heroIcon.get(SolidHeroIcons),
        disabled = disabled,
        groups = groups.asList(),
        content = content,
    )

    constructor(
        id: String,
        label: String,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg groups: PageGroup,
        disabled: Boolean = false,
        content: ContentBuilder? = null,
    ) : this(id, label, null, heroIcon, *groups, disabled = disabled, content = content)

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

public typealias PageGroup = List<Page>

public val Page.pages: List<Page> get() = groups.flatten()
