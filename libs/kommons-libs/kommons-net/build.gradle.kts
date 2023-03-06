plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.kotlin-jvm-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()
    sourceSets {
        commonMain {
            dependencies {
                api("com.bkahlert.kommons:kommons-core")
                api("com.bkahlert.kommons:kommons-time")
                api("com.bkahlert.kommons:kommons-uri")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json")
                api("io.ktor:ktor-http")
                api("io.ktor:ktor-utils") { because("generateNonce, Digest") }
                api("io.ktor:ktor-client-content-negotiation")
                api("io.ktor:ktor-client-core")
                api("io.ktor:ktor-client-logging")
                api("io.ktor:ktor-client-serialization")
                api("io.ktor:ktor-serialization-kotlinx-json")
            }
        }

        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
            }
        }

        jsMain {
            dependencies {
                api("io.ktor:ktor-client-js")
                api("io.ktor:ktor-client-auth")
                api(project(":kommons-js"))
            }
        }

        jvmMain {
            dependencies {
                api("com.bkahlert.kommons:kommons-logging-core")
                api("io.ktor:ktor-client-okhttp")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
