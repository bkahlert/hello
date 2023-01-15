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
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation(project(":hello-compose"))
                implementation(project(":semantic-ui-core"))
                implementation(project(":semantic-ui-elements"))
                implementation(project(":semantic-ui-collections"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(project(":semantic-ui-test"))
            }
        }
    }
}
