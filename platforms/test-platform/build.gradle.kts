plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    api(platform("io.kotest:kotest-bom:${libs.versions.kotest.get()}"))
    api(platform("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}"))
}
