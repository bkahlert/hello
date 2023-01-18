plugins { base }

tasks {

    val removeDsStore by registering(Exec::class) {
        group = "build"
        description = "Removes all .DS_Store files automatically created by macOS."
        commandLine("find", ".", "-type", "f", "-name", ".DS_Store", "-exec", "rm", "{}", ";")
    }

    clean.configure { dependsOn(removeDsStore) }

    listOf(assemble, build, clean, check).forEach { baseTask ->
        baseTask.configure {
            gradle.includedBuilds.forEach {
                dependsOn(it.task(":${baseTask.name}"))
            }
        }
    }
}
