plugins {
    id("com.bkahlert.kotlin-js-library")
    id("com.bkahlert.kotlin-serialization-json-feature")
}

group = "$group.kommons"

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                api(libs.bundles.ktor.js.client)
                api(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kommons)
                implementation(project(":kommons-web"))
            }
        }
    }
}
