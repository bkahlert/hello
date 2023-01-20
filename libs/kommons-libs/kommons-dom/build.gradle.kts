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
                implementation("com.bkahlert.kommons:kommons-text") { because("kebab-case") }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json") { because("storage delegate") }
            }
        }
    }
}
