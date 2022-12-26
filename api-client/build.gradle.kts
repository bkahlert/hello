plugins {
    id("com.bkahlert.compose-web-application")
}

group = "$group.hello"

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons")
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-deployment")
                implementation("com.bkahlert.kommons:kommons-ktor")
                implementation("com.bkahlert.kommons:kommons-web")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        val jsMain by getting {
            dependencies {
                api(libs.bundles.ktor.js.client)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
