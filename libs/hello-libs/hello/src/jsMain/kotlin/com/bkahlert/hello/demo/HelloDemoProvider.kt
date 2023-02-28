package com.bkahlert.hello.demo

import com.bkahlert.hello.app.demo.HelloAppDemoProvider
import com.bkahlert.hello.app.ui.HelloImageFixtures
import com.bkahlert.hello.environment.demo.EnvironmentViewDemos
import com.bkahlert.hello.props.demo.PropsViewDemos
import com.bkahlert.hello.search.demos.SearchDemoProvider
import com.bkahlert.hello.session.demo.SessionViewDemos
import com.bkahlert.hello.user.demo.UserMenuDemos
import com.bkahlert.semanticui.demo.DemoProvider

public val HelloDemoProviders: Array<DemoProvider> = arrayOf(
    HelloAppDemoProvider,
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
        { demoScope ->
            SearchDemoProvider.content.forEach { demoContent -> demoContent.invoke(this, demoScope) }
        },
    ),
)
