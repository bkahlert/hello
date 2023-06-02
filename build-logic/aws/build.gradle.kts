plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform("com.bkahlert.platform:plugins-platform"))
    implementation(project(":commons"))
    implementation(project(":kotlin-project"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("com.github.johnrengelman:shadow")
}
