package com.bkahlert.hello.demo

import com.bkahlert.hello.environment.demo.EnvironmentViewDemos
import com.bkahlert.hello.props.demo.PropsViewDemos
import com.bkahlert.hello.search.demos.SearchDemoProvider
import com.bkahlert.hello.session.demo.SessionViewDemos
import com.bkahlert.hello.user.demo.UserMenuDemos
import com.bkahlert.semanticui.demo.DemoProvider

public val HelloDemoProviders: Array<DemoProvider> = arrayOf(
    DemoProvider(
        id = "hello-apps",
        name = "Apps",
        logo = HelloImageFixtures.HelloFavicon,
        {

        },
    ),
    DemoProvider(
        id = "hello-views",
        name = "Views",
        logo = HelloImageFixtures.HelloFavicon,
        {
            EnvironmentViewDemos()
            SessionViewDemos()
            UserMenuDemos()
            PropsViewDemos()
        },
        {
            SearchDemoProvider.content.forEach { it.invoke(this) }
        },
    ),
)
