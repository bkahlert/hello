plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.kotlin-jvm-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.bkahlert.kommons:kommons-uri")
            }
        }

        val commonTest by getting
    }
}
