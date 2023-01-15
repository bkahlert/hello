plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":kommons-auth"))
                implementation(project(":kommons-json-ktor"))
                implementation(project(":kommons-logging-inline"))
                implementation("io.ktor:ktor-client-auth")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
