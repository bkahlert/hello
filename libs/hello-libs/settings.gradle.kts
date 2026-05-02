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

// == Define the inner structure of this component ==
rootProject.name = "hello-libs"
rootDir.listFiles { file ->
    file.resolve("build.gradle.kts").exists()
}?.forEach { projectDir ->
    include(projectDir.name)
}
