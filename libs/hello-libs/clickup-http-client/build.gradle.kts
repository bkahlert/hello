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
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")

                implementation(project(":clickup-model"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
