plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {
    api(platform(libs.kommons.bom))
    api(platform(libs.kotlin.bom))
    api(platform(libs.kotlin.wrapper.bom))
    api(platform(libs.kotlinx.coroutines.bom))
    api(platform(libs.kotlinx.serialization.bom))
    api(platform(libs.ktor.bom))

    // Delta state changes for flows, https://github.com/tunjid/Mutator
    constraints {
        val mutatorVersion = "0.0.7"
        api("com.tunjid.mutator:core:$mutatorVersion")
        api("com.tunjid.mutator:coroutines:$mutatorVersion")
    }

    // Reactive web apps library, similar to Compose but simpler, https://github.com/jwstegemann/fritz2
    constraints {
        val fritz2Version = "1.0-RC4"
        api("dev.fritz2:core:$fritz2Version")
        api("dev.fritz2:headless:$fritz2Version")
    }
}
