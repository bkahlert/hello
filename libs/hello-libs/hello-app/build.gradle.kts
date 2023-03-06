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
                api("com.bkahlert.kommons:kommons-core")
                api(project(":hello-environment"))
                api(project(":hello-session"))
                api(project(":hello-props"))
                api(project(":hello-user"))
                api("com.bkahlert.kommons:kommons-net") { because("LenientJson, JsonHttpClient") }
                api("com.bkahlert.semantic-ui:semantic-ui")
            }
        }
    }
}
