@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
include("commons")
include("kotlin-project")
include("kotlin-library")
include("kotlin-feature")
include("compose-web-application")
