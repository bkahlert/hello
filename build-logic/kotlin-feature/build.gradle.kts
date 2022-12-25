plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(project(":commons"))
    api(project(":kotlin-project"))
    api("org.jetbrains.kotlin:kotlin-serialization:1.7.21")
}
