plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(project(":hello-environment"))
                api(project(":hello-session"))
                api(project(":hello-props"))
                api(project(":hello-user"))
                api(project(":hello-search"))
            }
        }
        val jsTest by getting
    }
}
