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
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
includeBuild("../../platforms")
includeBuild("../../libs/hello-libs")
includeBuild("../../libs/kommons-libs")

// == Define the inner structure of this component ==
rootProject.name = "api-client"