plugins {
    id("com.bkahlert.compose-web-application")
    id("com.bkahlert.kotlin-serialization-json-feature")
}

group = "$group.hello"

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kommons)
                implementation("com.bkahlert.kommons:kommons-deployment")
                implementation("com.bkahlert.kommons:kommons-ktor")
                implementation("com.bkahlert.kommons:kommons-web")
            }
        }
    }
}
