import com.bkahlert.aws.cdk

plugins {
    id("com.bkahlert.aws.cdk-project")
    application
}

dependencies {
    implementation("com.bkahlert.kommons:kommons")
    implementation("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("com.bkahlert.hello.deployment.MainKt")
}

val cdkTasks = listOf(
    "list" to "Lists the stacks in the app",
    "bootstrap" to "Deploys the CDK Toolkit staging stack; see Bootstrapping",
    "diff" to "Compares the specified stack and its dependencies with the deployed stacks or a local CloudFormation template",
    "metadata" to "Displays metadata about the specified stack",
    "context" to "Manages cached context values",
    "docs" to "Opens the CDK API Reference in your browser",
    "doctor" to "Checks your CDK project for potential problems"
).associate { (command, description) ->
    command to tasks.register(command, Exec::class) {
        this.group = "cdk"
        this.description = description
        cdk(command)
    }
}

val synthesize by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Synthesizes and prints the CloudFormation template for one or more specified stacks"
    cdk("synthesize")
}

/** @see <a href="https://docs.aws.amazon.com/cdk/v2/guide/apps.html#lifecycle">CDK — App lifecycle</a> */
val deploy by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Deploys one or more specified stacks"
    cdk("deploy", "--all", "--require-approval", "never")
}

/** @see <a href="https://docs.aws.amazon.com/cdk/v2/guide/apps.html#lifecycle">CDK — App lifecycle</a> */
val deployNoRollback by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Deploys one or more specified stacks"
    cdk("deploy", "--all", "--require-approval", "never", "--no-rollback")
}

/** @see <a href="https://docs.aws.amazon.com/cdk/v2/guide/cli.html#cli-deploy-hotswap">Hot swapping</a> */
val deployHotswap by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Attempts to update resources directly instead of generating and deploying a CloudFormation changeset"
    cdk("deploy", "--all", "--require-approval", "never", "--hotswap", "--no-rollback")
}

/** @see <a href="https://docs.aws.amazon.com/cdk/v2/guide/cli.html#cli-deploy-hotswap">Hot swapping</a> */
val deployHotswapClickUp by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Attempts to update resources directly instead of generating and deploying a CloudFormation changeset"
    cdk("deploy", "ClickUp", "--require-approval", "never", "--hotswap", "--no-rollback")
}

/** @see <a href="https://docs.aws.amazon.com/cdk/v2/guide/cli.html#cli-deploy-watch">Watch mode</a> */
val deployWatch by tasks.registering(Exec::class) {
    group = "cdk"
    description = """
        Continuously monitors your CDK app's source files and assets for changes.
        It immediately performs a deployment of the specified stacks when a change is detected.
    """.trimIndent()
    cdk("deploy", "--all", "--watch", "--no-rollback")
}

val destroy by tasks.registering(Exec::class) {
    group = "cdk"
    description = "Destroys one or more specified stacks"
    cdk("destroy", "--all", "--force")
}
