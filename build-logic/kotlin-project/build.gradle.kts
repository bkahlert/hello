plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.bkahlert.platform:plugins-platform"))
    implementation(project(":commons"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.kotlin:kotlin-serialization")
}
