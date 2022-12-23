plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":commons"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.3.0-rc01")
}
