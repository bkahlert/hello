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
                api("org.jetbrains.kotlinx:kotlinx-serialization-core")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
