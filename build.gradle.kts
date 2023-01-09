tasks.register("clean") {
    group = "build"
    description = "Deletes the build directories."
    gradle.includedBuilds
        .forEach { includedBuild ->
            when (includedBuild.name) {
                "platforms" -> {
                    dependsOn(includedBuild.task(":aws-platform:clean"))
                    dependsOn(includedBuild.task(":plugins-platform:clean"))
                    dependsOn(includedBuild.task(":product-platform:clean"))
                    dependsOn(includedBuild.task(":test-platform:clean"))
                }

                "build-logic" -> {
                    dependsOn(includedBuild.task(":aws:clean"))
                    dependsOn(includedBuild.task(":commons:clean"))
                    dependsOn(includedBuild.task(":compose-web-application:clean"))
                    dependsOn(includedBuild.task(":kotlin-project:clean"))
                }

                "hello-libs" -> {
                }

                "kommons-libs" -> {
                    dependsOn(includedBuild.task(":kommons-auth:clean"))
                    dependsOn(includedBuild.task(":kommons-auth-ktor:clean"))
                    dependsOn(includedBuild.task(":kommons-dom:clean"))
                }

                "api-client" -> dependsOn(includedBuild.task(":clean"))
                "test-app" -> dependsOn(includedBuild.task(":clean"))
                "web-app" -> dependsOn(includedBuild.task(":clean"))
                "aws-cdk" -> dependsOn(includedBuild.task(":app:clean"))
                "aws-lambdas" -> {
                    dependsOn(includedBuild.task(":base:clean"))
                    dependsOn(includedBuild.task(":base-test:clean"))
                    dependsOn(includedBuild.task(":clickup-api-handlers:clean"))
                    dependsOn(includedBuild.task(":minimal:clean"))
                    dependsOn(includedBuild.task(":minimal-base:clean"))
                    dependsOn(includedBuild.task(":userinfo-api-handlers:clean"))
                    dependsOn(includedBuild.task(":userprops-api-handlers:clean"))
                }
            }
        }
}

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
