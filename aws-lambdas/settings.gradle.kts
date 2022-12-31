@file:Suppress("UnstableApiUsage")

import java.io.FileFilter

// == Define locations for build logic ==
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../build-logic")
}

// == Define locations for components ==
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// == Define the inner structure of this component ==
rootProject.name = "aws-lambdas"
rootDir.listFiles(FileFilter { file ->
    file.resolve("build.gradle.kts").exists()
})?.forEach { projectDir ->
    include(projectDir.name)
}
