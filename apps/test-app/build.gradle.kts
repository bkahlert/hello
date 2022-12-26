plugins {
    id("com.bkahlert.compose-web-application")
}

group = "$group.test-app"

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-web")
            }
        }
    }
}
