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
                api("com.bkahlert.kommons:kommons-text")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json")
                implementation(project(":kommons-logging-inline"))
            }
        }
    }
}
