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
                implementation(project(":semantic-ui-core"))
                implementation(project(":semantic-ui-elements"))
                implementation(project(":semantic-ui-collections"))
            }
        }
    }
}
