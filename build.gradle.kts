plugins { base }

listOf("assemble", "build", "clean", "check").forEach { baseTaskName ->
    tasks.named(baseTaskName).configure {
        gradle.includedBuilds.forEach {
            dependsOn(it.task(":$baseTaskName"))
        }
    }
}
