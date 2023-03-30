package com.bkahlert.hello

import com.bkahlert.hello.applets.Applets.Companion.applets
import com.bkahlert.hello.fritz2.app.props.PropsStore
import com.bkahlert.hello.fritz2.app.session.SessionStore
import com.bkahlert.hello.fritz2.app.user.User
import com.bkahlert.hello.fritz2.components.loader
import dev.fritz2.core.RenderContext

fun RenderContext.app() {
    div("h-screen pt-16 -mt-16") {
        div("flex justify-center items-center w-full h-full") {
            loader("Loading ...", classes = "w-16 h-16 opacity-15 animate-ping")
        }
    }
}

fun RenderContext.app(
    sessionStore: SessionStore,
    propsStore: PropsStore,
) {
    propsStore.applets().render(this)
}

fun RenderContext.app(
    userStore: SessionStore,
    user: User,
    propsStore: PropsStore,
) {
    propsStore.applets().render(this)
}
