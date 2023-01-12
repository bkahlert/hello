tasks.register("clean") {
    group = "build"
    description = "Deletes the build directories."
    subprojects.forEach {
        dependsOn(it.tasks.named("clean"))
    }
}

tasks.register("check") {
    group = "verification"
    description = "Runs all checks."
    subprojects.forEach {
        dependsOn(it.tasks.named("check"))
    }
}
