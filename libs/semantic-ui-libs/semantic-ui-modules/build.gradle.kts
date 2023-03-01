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
                api("com.bkahlert.kommons:kommons-js")
                api(project(":semantic-ui-core"))
                api(project(":semantic-ui-elements"))
                api(project(":semantic-ui-collections"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(project(":semantic-ui-test"))
            }
        }
    }
}
