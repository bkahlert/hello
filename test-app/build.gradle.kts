plugins {
    id("com.bkahlert.compose-web-application")
}

group = "$group.test-app"

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-web")
            }
        }
    }
}
