@file:Suppress("UnstableApiUsage")

// == Define locations for components ==
dependencyResolutionManagement {
    repositories {
        mavenLocal()
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
rootDir.listFiles { file ->
    file.resolve("build.gradle.kts").exists()
}?.forEach { projectDir ->
    include(projectDir.name)
}
