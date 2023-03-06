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
                api("com.bkahlert.kommons:kommons-dom") { because("LocationFragmentParameters") }
                api("com.bkahlert.kommons:kommons-js") { because("ConsoleLogger") }
                api("com.bkahlert.kommons:kommons-text") { because("demos") }
                api("com.bkahlert.kommons:kommons-uri") { because("Uri type, devmode") }
                api(project(":semantic-ui-core"))
                api(project(":semantic-ui-elements"))
                api(project(":semantic-ui-collections"))
                api(project(":semantic-ui-modules"))
                api(project(":semantic-ui-views"))
                api(project(":semantic-ui-custom"))
            }
        }
    }
}
