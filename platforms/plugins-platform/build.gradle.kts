plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}") { because("Kotlin-based projects") }
        api("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.get()}") { because("serialization") }
        api("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.compose.get()}") { because("Frontends based on Compose for Web") }
        api("gradle.plugin.com.github.johnrengelman:shadow:${libs.versions.shadow.get()}") { because("fat jar creation for AWS lambdas") }
    }
}
