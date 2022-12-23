plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":kotlin-project"))
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.3.0-rc01")
}
