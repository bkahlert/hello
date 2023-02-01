plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.semantic-ui:semantic-ui")
                implementation("com.bkahlert.hello:clickup")
                implementation("com.bkahlert.hello:hello-client")
                implementation("com.bkahlert.hello:hello-search")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
