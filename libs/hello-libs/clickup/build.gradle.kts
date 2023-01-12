plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")

                implementation(project(":url"))
            }
        }
    }
}
