plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project") // TODO remove
    id("com.bkahlert.fritz2-project")
}

group = "$group.hello"

kotlin {

    sourceSets {
        jsMain {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.semantic-ui:semantic-ui")
                implementation("com.bkahlert.hello:clickup")
                implementation("com.bkahlert.hello:hello")
                implementation("com.bkahlert.hello:hello-fritz2")
                implementation("com.bkahlert.hello:hello-fritz2-compose")

                implementation(devNpm("less", "^4.1")) { because("dynamic stylesheet language") }
                implementation(devNpm("less-loader", "^11.1")) { because("Less to CSS compilation") }
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
