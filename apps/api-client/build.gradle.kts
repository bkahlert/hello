plugins {
    id("com.bkahlert.compose-web-application")
}

group = "$group.hello"

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-json")
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")
                implementation("com.bkahlert.hello:hello-client")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
