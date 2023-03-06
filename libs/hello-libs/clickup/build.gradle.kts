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
                api("com.bkahlert.semantic-ui:semantic-ui")
                api(project(":clickup-model"))
                api(project(":clickup-view"))
                api(project(":clickup-viewmodel"))
                api(project(":clickup-http-client"))
            }
        }
    }
}
