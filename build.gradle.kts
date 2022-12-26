tasks.register("check") {
    group = "verification"
    description = "Run all checks"
    val excluded = listOf("platforms", "build-logic", "hello-libs")
    gradle.includedBuilds
        .filterNot { it.name in excluded }
        .forEach { includedBuild ->
            dependsOn(includedBuild.task(":check"))
        }
}
