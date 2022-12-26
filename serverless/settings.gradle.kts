@file:Suppress("UnstableApiUsage")

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
//includeBuild("../platforms")
//includeBuild("../web-app")

// == Define the inner structure of this component ==
rootProject.name = "serverless"
