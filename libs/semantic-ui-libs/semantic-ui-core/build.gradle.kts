plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.semantic-ui"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                api("com.bkahlert.kommons:kommons-dom")
                api("com.bkahlert.kommons:kommons-js")
                api(npm("semantize2", "^1.0.1")) { because("custom packaging of jQuery and Semantic UI styles / modules") }
            }
        }
    }
}
