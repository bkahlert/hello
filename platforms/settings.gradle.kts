@file:Suppress("UnstableApiUsage")

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

rootProject.name = "platforms"

include("product-platform")
include("test-platform")
include("plugins-platform")
