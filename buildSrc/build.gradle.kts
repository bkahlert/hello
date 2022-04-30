plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation("com.bkahlert.kommons:kommons:1.12.0-dev.6.uncommitted+17a6503")
    implementation("org.yaml:snakeyaml:1.30")
    implementation("org.hidetake.ssh:org.hidetake.ssh.gradle.plugin:2.10.1")
}
