plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    api(platform(libs.kotlin.bom))
    api(platform(libs.kotlinx.coroutines.bom))
    api(platform(libs.kotlinx.serialization.bom))
    api(platform(libs.ktor.bom))

    constraints {
        api("com.bkahlert.kommons:kommons:2.5.0")
        api("com.bkahlert.kommons:kommons-debug:2.5.0")
    }
}
