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
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")
                implementation("com.bkahlert.kommons:kommons-text")

                implementation(project(":url"))
                api(project(":clickup-model"))
                api(project(":clickup-client"))
            }
        }
    }
}
