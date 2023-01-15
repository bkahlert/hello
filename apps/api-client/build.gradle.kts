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
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-json")
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")
                implementation("com.bkahlert.hello:hello-client")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
