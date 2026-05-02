plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.fritz2-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                api(project(":hello-fritz2"))
                api(project(":clickup-model"))
                api(project(":hello-page"))
                api(project(":hello-showcase"))
                implementation(project(":clickup-ui-compose"))
            }
        }
    }
}
