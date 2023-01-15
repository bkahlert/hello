@file:Suppress("UnstableApiUsage")

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
        mavenLocal()
        mavenCentral()
    }
}

// == Define the inner structure of this component ==
rootProject.name = "aws-lambdas"
rootDir.listFiles { file ->
    file.resolve("build.gradle.kts").exists()
}?.forEach { projectDir ->
    include(projectDir.name)
}
