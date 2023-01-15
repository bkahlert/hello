plugins {
    id("com.bkahlert.kotlin-js-browser-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":hello-color"))
            }
        }
    }
}
