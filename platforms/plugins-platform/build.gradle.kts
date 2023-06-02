plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.asProvider().get()}") { because("Kotlin-based projects") }
        api("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.asProvider().get()}") { because("serialization") }
        api("com.github.johnrengelman:shadow:8.1.1") { because("fat jar creation for AWS lambdas") }
    }
}
