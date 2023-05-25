@file:Suppress("UnstableApiUsage")

// == Define locations for build logic ==
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../../build-logic")
}

// == Define locations for components ==
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
includeBuild("../../platforms")
includeBuild("../../libs/kommons-libs")
includeBuild("../../libs/hello-libs")

// == Define the inner structure of this component ==
rootProject.name = "playground-app"
