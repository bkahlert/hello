@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.bkahlert.kotlin-js-library")
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "$group.kommons"

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kommons)
            }

            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}
