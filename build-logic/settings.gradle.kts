@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}
includeBuild("../platforms")

rootProject.name = "build-logic"
include("commons")
include("kotlin-project")
include("compose-web-application")
include("aws")
