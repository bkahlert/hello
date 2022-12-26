plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    constraints {
        api("com.bkahlert.kommons:kommons-test:2.5.0")
    }
}
