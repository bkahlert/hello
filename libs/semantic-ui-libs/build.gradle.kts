plugins { base }

listOf(tasks.assemble, tasks.build, tasks.clean, tasks.check).forEach { baseTask ->
    baseTask.configure {
        subprojects.forEach {
            dependsOn(it.tasks.named(baseTask.name))
        }
    }
}
