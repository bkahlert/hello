package com.bkahlert.hello.components

import com.bkahlert.hello.clickup.ClickUpProps
import com.bkahlert.hello.clickup.clickUpMenu
import com.bkahlert.hello.fritz2.SyncStore
import com.bkahlert.hello.fritz2.app.props.PropStoreFactory
import com.bkahlert.hello.fritz2.app.props.PropsStore
import dev.fritz2.core.RenderContext
import kotlinx.serialization.serializer

value class ClickUp(val store: SyncStore<ClickUpProps>) : SyncStore<ClickUpProps> by store {

    fun render(renderContext: RenderContext) = renderContext.div("flex-1 min-w-0 max-w-2xl") {
        data.render { props -> clickUpMenu(props) }
    }

    override fun toString(): String {
        return "ClickUp(links=$current)"
    }

    companion object : PropStoreFactory<ClickUpProps> {
        override val DEFAULT_KEY: String = "clickup"
        override val DEFAULT_VALUE: ClickUpProps = ClickUpProps(null)

        override fun invoke(propsStore: PropsStore, defaultValue: ClickUpProps, id: String): ClickUp =
            ClickUp(propsStore.map(id, defaultValue, serializer()))
    }
}
