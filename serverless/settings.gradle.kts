@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

// == Define locations for build logic ==
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// == Define locations for components ==
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../web-app")

// == Define the inner structure of this component ==
rootProject.name = "serverless"
