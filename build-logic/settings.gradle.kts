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
include("compose-web-application")
