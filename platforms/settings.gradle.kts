@file:Suppress("UnstableApiUsage")

import java.io.FileFilter

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
rootDir.listFiles(FileFilter { file ->
    file.resolve("build.gradle.kts").exists()
})?.forEach { projectDir ->
    include(projectDir.name)
}
