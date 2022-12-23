@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

// == Define locations for build logic ==
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// == Define locations for components ==
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

// == Define the inner structure of this component ==
rootProject.name = "web-app"
