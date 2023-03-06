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
                implementation("com.bkahlert.kommons:kommons-core")
                api("com.bkahlert.semantic-ui:semantic-ui")
            }
        }
    }
}
