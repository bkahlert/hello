@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}
includeBuild("../platforms")

rootProject.name = "build-logic"
rootDir.listFiles { file ->
    file.resolve("build.gradle.kts").exists()
}?.forEach { projectDir ->
    include(projectDir.name)
}
