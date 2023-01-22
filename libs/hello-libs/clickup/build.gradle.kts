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
                api("com.bkahlert.semantic-ui:semantic-ui")
                api(project(":clickup-model"))
                api(project(":clickup-view"))
                api(project(":clickup-viewmodel"))
                api(project(":clickup-http-client"))
            }
        }
        val jsTest by getting
    }
}
