plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.kotlin-jvm-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-json")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js") { because("Url type") }
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp") { because("Url type") }
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
