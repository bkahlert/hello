plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.aws")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons")
                implementation(project(":kommons-auth"))
                implementation(project(":kommons-web"))
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
