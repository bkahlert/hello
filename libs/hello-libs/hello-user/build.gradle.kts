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
                api("com.bkahlert.kommons:kommons-core")
                api(project(":hello-data"))
                api(project(":hello-environment"))
                api(project(":hello-session"))
                api(project(":hello-props"))
                api("com.bkahlert.kommons:kommons-net") { because("LenientJson, JsonHttpClient") }
                api("com.bkahlert.semantic-ui:semantic-ui")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("com.bkahlert.semantic-ui:semantic-ui-test")
            }
        }
    }
}
