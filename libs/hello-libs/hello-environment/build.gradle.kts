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
                api("com.bkahlert.kommons:kommons-net") { because("LenientJson, JsonHttpClient") }
                api(project(":hello-data"))
                api("com.bkahlert.semantic-ui:semantic-ui")
            }
        }
    }
}
