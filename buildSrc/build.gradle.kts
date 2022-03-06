plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.bkahlert.kommons:kommons:1.11.5")
    implementation("org.yaml:snakeyaml:1.30")
    implementation("org.hidetake.ssh:org.hidetake.ssh.gradle.plugin:2.10.1")
//    implementation("org.hidetake.ssh:org.hidetake.ssh.gradle.plugin:2.10.1")
}
