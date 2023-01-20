plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.semantic-ui"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(project(":semantic-ui-core"))
                api(project(":semantic-ui-elements"))
                api(project(":semantic-ui-collections"))
                api(project(":semantic-ui-modules"))
                api(project(":semantic-ui-views"))
                api(project(":semantic-ui-custom"))
            }
        }
        val jsTest by getting
    }
}
