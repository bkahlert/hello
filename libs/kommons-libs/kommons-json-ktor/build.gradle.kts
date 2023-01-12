plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.kotlin-jvm-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.ktor:ktor-client-content-negotiation")
                api("io.ktor:ktor-client-core")
                api("io.ktor:ktor-client-logging")
                api("io.ktor:ktor-client-serialization")
                api("io.ktor:ktor-serialization-kotlinx-json")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        val jsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js")
            }
        }

        val jvmMain by getting {
            dependencies {
                api("io.ktor:ktor-client-okhttp")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
