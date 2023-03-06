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
                api("com.bkahlert.kommons:kommons-color")
                api("com.bkahlert.kommons:kommons-dom")
                api("com.bkahlert.kommons:kommons-time")
                api("com.bkahlert.semantic-ui:semantic-ui")
                api(project(":clickup-model"))
                api(project(":clickup-view"))
            }
        }
    }
}
