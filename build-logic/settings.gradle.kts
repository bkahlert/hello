@file:Suppress("UnstableApiUsage")

import java.io.FileFilter

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}
includeBuild("../platforms")

rootProject.name = "build-logic"
rootDir.listFiles(FileFilter { file ->
    file.resolve("build.gradle.kts").exists()
})?.forEach { projectDir ->
    include(projectDir.name)
}
