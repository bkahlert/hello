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
                api("com.tunjid.mutator:core") { because("state production pipeline creation with flows") }
                api("com.tunjid.mutator:coroutines") { because("state production pipeline creation with flows") }
            }
        }
    }
}
