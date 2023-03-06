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
        google()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
includeBuild("../../platforms")
includeBuild("../../libs/kommons-libs")
includeBuild("../../libs/semantic-ui-libs")
includeBuild("../../libs/hello-libs")

// == Define the inner structure of this component ==
rootProject.name = "playground-app"
