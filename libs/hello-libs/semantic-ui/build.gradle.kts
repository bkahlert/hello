plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":semantic-ui-core"))
                api(project(":semantic-ui-elements"))
                api(project(":semantic-ui-collections"))
                api(project(":semantic-ui-modules"))
                api(project(":semantic-ui-views"))
            }
        }
    }
}
