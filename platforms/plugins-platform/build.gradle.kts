plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.asProvider().get()}") { because("Kotlin-based projects") }
        api("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.asProvider().get()}") { because("serialization") }
        api("gradle.plugin.com.github.johnrengelman:shadow:${libs.versions.shadow.get()}") { because("fat jar creation for AWS lambdas") }
    }
}
