plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.bkahlert.platform:plugins-platform"))
    implementation(project(":commons"))
    implementation(project(":kotlin-project"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.3.0-rc01")
}
