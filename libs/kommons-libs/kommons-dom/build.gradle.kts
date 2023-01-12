plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-text")
                implementation(project(":kommons-json"))
                implementation(project(":kommons-logging-inline"))
            }
        }
    }
}
