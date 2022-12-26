@file:Suppress("UnstableApiUsage")

import java.io.FileFilter

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
    }
}
includeBuild("../../platforms")

// == Define the inner structure of this component ==
rootProject.name = "hello-libs"
rootDir.listFiles(FileFilter { file ->
    file.resolve("build.gradle.kts").exists()
})?.forEach { projectDir ->
    include(projectDir.name)
}
