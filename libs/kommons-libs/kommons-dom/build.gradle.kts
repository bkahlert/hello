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
                api(project(":kommons-js"))
                api("com.bkahlert.kommons:kommons-uri")
            }
        }
    }
}
