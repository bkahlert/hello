plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.test-app"

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
