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
                api("com.bkahlert.kommons:kommons-net")
            }
        }
    }
}
