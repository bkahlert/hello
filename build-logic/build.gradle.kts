plugins { base }

listOf("assemble", "build", "clean", "check").forEach { baseTaskName ->
    tasks.named(baseTaskName).configure {
        subprojects.forEach {
            dependsOn(it.tasks.named(baseTaskName))
        }
    }
}
