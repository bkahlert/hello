plugins {
    id("com.bkahlert.compose-web-application")
    kotlin("plugin.serialization") version "1.7.21"
}

group = "$group.hello"

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.bundles.ktor.js.client)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kommons)
                implementation("com.bkahlert.kommons:kommons-web")
            }
        }
    }
}
