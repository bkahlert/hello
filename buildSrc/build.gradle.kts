plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    kotlin("reflect", "1.5.30")
    implementation("org.yaml:snakeyaml:1.30")
    implementation("org.hidetake.ssh:org.hidetake.ssh.gradle.plugin:2.10.1")
}
