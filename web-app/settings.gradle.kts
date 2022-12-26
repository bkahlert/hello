@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

// == Define locations for build logic ==
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../build-logic")
}

// == Define locations for components ==
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
includeBuild("../platforms")
includeBuild("../libs")

// == Define the inner structure of this component ==
rootProject.name = "web-app"
