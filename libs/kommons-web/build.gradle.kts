plugins {
    id("com.bkahlert.kotlin-js-library")
    id("com.bkahlert.kotlin-serialization-json-feature")
}

group = "$group.kommons"

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kommons)
            }
        }
    }
}
