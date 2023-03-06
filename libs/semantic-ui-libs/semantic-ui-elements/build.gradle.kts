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
                api(project(":semantic-ui-core"))
            }
        }
    }
}
