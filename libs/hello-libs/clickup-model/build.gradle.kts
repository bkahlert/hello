plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                api("com.bkahlert.kommons:kommons-core")
                api("com.bkahlert.kommons:kommons-color")
                api("com.bkahlert.kommons:kommons-uri")
                implementation("com.bkahlert.kommons:kommons-inc")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
