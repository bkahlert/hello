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

tasks.register("buildApps") {
    group = "build"
    description = "Builds all apps"
    listOf(
        "api-client",
        "test-app",
        "web-app",
    ).forEach {
        dependsOn(gradle.includedBuild(it).task(":jsBrowserProductionWebpack"))
    }
}

tasks.register("buildLambdas") {
    group = "build"
    description = "Builds all Lambda functions"
    listOf(
        ":clickup-api-handlers",
        ":userinfo-api-handlers",
        ":userprops-api-handlers",
    ).forEach {
        dependsOn(gradle.includedBuild("aws-lambdas").task("$it:shadowJar"))
    }
}
