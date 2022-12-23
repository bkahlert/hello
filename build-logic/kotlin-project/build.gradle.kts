plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":commons"))
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
}
