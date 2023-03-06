plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                api(project(":hello-environment"))
                api(project(":hello-session"))
                api(project(":hello-props"))
                api(project(":hello-user"))
                api(project(":hello-search"))
                api(project(":hello-app"))
            }
        }
    }
}
