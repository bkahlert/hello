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
                api("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-util")
                api("com.bkahlert.semantic-ui:semantic-ui")
                api(project(":clickup-model"))
                api(project(":clickup-view"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("com.bkahlert.semantic-ui:semantic-ui-test")
            }
        }
    }
}
