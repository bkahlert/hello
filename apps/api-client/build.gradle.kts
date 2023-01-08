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
                implementation("com.bkahlert.kommons:kommons-ktor-json")
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-web")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-auth")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-js")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-serialization")
                implementation("io.ktor:ktor-serialization-kotlinx-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
