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
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
includeBuild("../platforms")

// == Define the inner structure of this component ==
rootProject.name = "libs"
include("kommons-auth")
include("kommons-deployment")
include("kommons-ktor")
include("kommons-web")
