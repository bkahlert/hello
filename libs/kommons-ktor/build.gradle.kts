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
                api(libs.bundles.ktor.js.client)
                api(libs.ktor.serialization.kotlinx.json)
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
