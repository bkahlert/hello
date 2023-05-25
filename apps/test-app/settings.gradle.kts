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

// == Define the inner structure of this component ==
rootProject.name = "test-app" // the component name
