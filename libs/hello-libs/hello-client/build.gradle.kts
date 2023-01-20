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
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.kommons:kommons-text")

                implementation(project(":clickup-model"))
//                api(project(":clickup-client"))
            }
        }
    }
}
