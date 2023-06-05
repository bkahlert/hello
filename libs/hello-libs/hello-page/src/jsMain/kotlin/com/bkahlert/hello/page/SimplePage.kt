package com.bkahlert.hello.page

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.hello.icon.heroicons.HeroIcons
import com.bkahlert.hello.icon.heroicons.OutlineHeroIcons
import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.kommons.uri.Uri
import org.w3c.dom.HTMLDivElement
import kotlin.reflect.KProperty1

public open class SimplePage(
    public override val id: String,
    override val name: String,
    public override val description: String? = null,
    override val icon: Uri,
    override val activeIcon: Uri = icon,
    pages: List<Page> = emptyList(),
    override val content: ContentBuilder<HTMLDivElement>? = null,
) : ParentPage {

    public constructor(
        id: String,
        label: String,
        description: String?,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg pages: Page,
        content: ContentBuilder<HTMLDivElement>? = null,
    ) : this(
        id = id,
        name = label,
        description = description,
        icon = heroIcon.get(OutlineHeroIcons),
        activeIcon = heroIcon.get(SolidHeroIcons),
        pages = pages.asList(),
        content = content,
    )

    public constructor(
        id: String,
        label: String,
        heroIcon: KProperty1<HeroIcons, DataUri>,
        vararg pages: Page,
        content: ContentBuilder<HTMLDivElement>? = null,
    ) : this(id, label, null, heroIcon, *pages, content = content)

    public override val pages: List<ChildPage> = pages.map {
        object : ChildPage, Page by it {
            override val parent: ParentPage
                get() = this@SimplePage
        }
    }
}
