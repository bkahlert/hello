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

                implementation(project(":hello-dom"))
                implementation(project(":hello-color"))
                implementation(project(":hello-url"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
