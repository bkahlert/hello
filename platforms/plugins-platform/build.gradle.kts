plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

dependencies {
    constraints {
//        api("org.springframework.boot:org.springframework.boot.gradle.plugin:2.4.0")
        api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
        api("org.jetbrains.kotlin:kotlin-serialization:1.7.21")
        api("org.jetbrains.compose:compose-gradle-plugin:1.3.0-rc01")
    }
}
