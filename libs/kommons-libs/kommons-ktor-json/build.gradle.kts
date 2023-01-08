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
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-serialization")
                implementation("io.ktor:ktor-serialization-kotlinx-json")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
