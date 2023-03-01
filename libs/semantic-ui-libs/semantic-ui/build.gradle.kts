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
                api("com.bkahlert.kommons:kommons-dom") { because("LocationFragmentParameters") }
                api("com.bkahlert.kommons:kommons-js") { because("ConsoleLogger") }
                implementation("com.bkahlert.kommons:kommons-text") { because("demos") }
                implementation("com.bkahlert.kommons:kommons-uri") { because("Uri type, devmode") }
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
