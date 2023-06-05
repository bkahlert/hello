package com.bkahlert.hello.page

import com.bkahlert.hello.fritz2.ContentBuilder
import com.bkahlert.kommons.uri.Uri
import org.w3c.dom.HTMLDivElement

public interface Page {
    public val id: String
    public val name: String
    public val description: String? get() = null
    public val icon: Uri
    public val activeIcon: Uri get() = icon
    public val content: ContentBuilder<HTMLDivElement>? get() = null
}

public interface ParentPage : Page {
    public val pages: List<Page>
}

public interface ChildPage : Page {
    public val parent: ParentPage
}

@Suppress("RecursivePropertyAccessor")
public val Page.path: List<Page>
    get() = if (this is ChildPage) parent.path + this else listOf(this)
