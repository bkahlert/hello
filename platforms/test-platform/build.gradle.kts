plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    api(platform("io.kotest:kotest-bom:5.5.4"))
    api(platform("org.testcontainers:testcontainers-bom:1.17.6"))
}
