dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "build-logic"
include("commons")
include("kotlin-library")
include("compose-web-application")
