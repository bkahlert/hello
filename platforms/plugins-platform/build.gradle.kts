plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.asProvider().get()}")
        api("org.jetbrains.kotlin:kotlin-serialization:${libs.versions.kotlin.asProvider().get()}")
        api("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.compose.get()}")
        api("gradle.plugin.com.github.johnrengelman:shadow:${libs.versions.shadow.get()}")
    }
}
