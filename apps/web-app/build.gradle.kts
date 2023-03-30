plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.fritz2-project")
    id("com.bkahlert.aws.app")
}

group = "$group.hello"

kotlin {

    sourceSets {
        jsMain {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.hello:hello-fritz2")
                implementation(npm("encrypt-storage", "^2.12")) { because("localStorage encryption for offline props copy") }
                implementation("com.bkahlert.hello:clickup")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
