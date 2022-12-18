//@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.cli.common.repl.replRemoveLineBreaksInTheEnd
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.io.FilenameFilter
import java.nio.file.Files
import java.util.Base64

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "com.serverless"
version = "0.1.0"
description = "hello"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java { sourceCompatibility = JavaVersion.toVersion("11") }

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.bundles.aws.lambda.java)
    implementation(libs.bundles.aws.sdk.kotlin)
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")

    implementation(libs.kommons)
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.8.0")

    testImplementation(libs.kommons.test)
    testImplementation(libs.aws.lambda.java.tests)
}

tasks.jar {
    archiveVersion.set("")
}
tasks.shadowJar {
    archiveVersion.set("")
    mergeServiceFiles()
    transform(Log4j2PluginsCacheFileTransformer::class.java)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    filter {
        isFailOnNoMatchingTests = false
    }
}


/* AWS */

val AWS_GROUP = "aws"
val AWS_LAMBDA_GROUP = "aws λ"
fun Exec.aws(vararg args: String): Exec =
    commandLine("aws", *args)

fun Exec.awsLambda(vararg args: String): Exec =
    aws("lambda", *args)


/* serverless */

fun Exec.serverless(vararg args: String): Exec =
    commandLine("serverless", *args)

fun Exec.serverlessDeploy(vararg args: String): Exec =
    serverless("deploy", *args)

val stage by extra("dev")
val service = "hello" // TODO load from serverless.yaml, see Vaults.kt
val functionToEventsMapping: Map<String, List<File>> = sourceSets.map {
    it.resources.matching { include("**/events/*/*.json") }.groupBy { it.parentFile.name }
}.fold(mapOf<String, List<File>>()) { acc, map ->
    acc.keys.plus(map.keys).associateWith { key -> setOfNotNull(acc[key], map[key]).flatten() }
}

val SERVERLESS_GROUP = "serverless"

val buildWebApp by tasks.registering {
    group = SERVERLESS_GROUP
    description = "Builds the web app."
    val webAppProject = gradle.includedBuild("web-app")
    dependsOn(webAppProject.task(":jsBrowserProductionWebpack"))
    doLast {
        val output = project.buildDir.resolve("web-app")
        val indexDocument = output.resolve("index.html")

        val distributionsDirectory = file(webAppProject.projectDir.resolve("build/distributions"))
        check(distributionsDirectory.exists()) { "Failed to determine distributions directory" }

        check(output.deleteRecursively()) { "Failed to clean $output" }
        Files.createDirectory(output.toPath())
        distributionsDirectory.copyRecursively(output)
        output.listFiles(FilenameFilter { _, name -> name.endsWith(".dev.json") })
            ?.forEach { it.delete() }

        check(indexDocument.exists()) { "Cannot find $indexDocument" }
    }
}

val deploy by tasks.registering(Exec::class) {
    group = SERVERLESS_GROUP
    description = "Deploys the whole project."
    dependsOn(tasks.shadowJar, buildWebApp)
    serverlessDeploy("--aws-s3-accelerate")
}

tasks.register<Exec>("remove") {
    group = SERVERLESS_GROUP
    description = listOf(
        "Deletes all the AWS resources created by your project and ensures that",
        "you don't incur any unexpected charges.",
        "It will also remove the service from Serverless Dashboard.",
    ).joinToString(" ")
    serverless("remove")
}

functionToEventsMapping.forEach { (fn, eventFiles) ->
    val deploy = tasks.register<Exec>("deploy-$fn") {
        group = SERVERLESS_GROUP
        description = "Deploys the lambda function $fn."
        dependsOn(tasks.shadowJar)
        serverlessDeploy("function", "--function", fn)
    }

    listOf<File?>(null).plus(eventFiles).forEach { eventFile ->
        val name = eventFile?.nameWithoutExtension
        tasks.register<Exec>("invoke-$fn${name?.let { "+$it" } ?: ""}") {
            group = "$SERVERLESS_GROUP-$stage/$fn"
            description = "Invokes the lambda function $fn${name?.let { " with event $it" } ?: ""}."
            dependsOn(deploy)
            serverless("invoke", "--function", fn, *eventFile?.let { arrayOf("--path", "$eventFile") } ?: emptyArray())
        }
        tasks.register<Exec>("invoke-local-$fn${name?.let { "+$it" } ?: ""}") {
            group = "$SERVERLESS_GROUP-local/$fn"
            description = "Locally invokes the lambda function $fn${name?.let { " with event $it" } ?: ""}."
            dependsOn(tasks.shadowJar)
            serverless("invoke", "local", "--function", fn, *eventFile?.let { arrayOf("--path", "$eventFile") } ?: emptyArray())
        }
    }

    val logs = tasks.register<Exec>("logs-$fn") {
        group = SERVERLESS_GROUP
        description = "Lets you watch the logs of the lambda function $fn."
        serverless("logs", "--function", fn)
    }

    val logsTail = tasks.register<Exec>("logsTail-$fn") {
        group = SERVERLESS_GROUP
        description = "Lets you tail the logs of the lambda function $fn."
        serverless("logs", "--function", fn, "--tail")
    }

    val invokeWithAws = tasks.register<Exec>("awsInvoke-$fn") {
        group = AWS_LAMBDA_GROUP
        description = "Invokes the lambda function $fn."
        val tmp: File? = null
        val executionOutputFile = File.createTempFile("out.", ".$fn", tmp)
        commandLine(
            "aws", "lambda", "invoke",
            "--function-name", "$service-$stage-$fn",
            "--cli-binary-format", "raw-in-base64-out",
            "--payload",
            """
                {
                  "key": "value"
                }
            """.trimIndent(),
            executionOutputFile.absolutePath,
            "--log-type", "Tail",
            "--query", "LogResult",
            "--output", "text"
        )
        val logOutputByteArrayOutputStream = ByteArrayOutputStream()
        standardOutput = logOutputByteArrayOutputStream
        doLast {
            logger.lifecycle("λ Log {}", name)
            val logOutput = String(Base64.getDecoder().decode(logOutputByteArrayOutputStream.toString().replRemoveLineBreaksInTheEnd()))
            logger.lifecycle(buildString {
                val lines = logOutput.replRemoveLineBreaksInTheEnd().lines()
                lines.withIndex().forEach { (i, line) ->
                    append("  ")
                    when (i) {
                        0 -> appendLine(line.substringBefore(" "))
                        lines.size - 2 -> append(line.substringBefore(" "))
                        lines.size - 1 -> {
                            line.trim().substringAfter(" ")
                                .split("\t")
                                .dropWhile { it.startsWith("requestid", ignoreCase = true) }
                                .joinTo(this, " - ")
                            appendLine()
                        }

                        else -> appendLine(line)
                    }
                }
            })

            logger.lifecycle("λ Response {}", name)
            val jsonFormattedOutputExecutionFile = File.createTempFile("out.", ".$fn.json", tmp)
            ProcessBuilder("bash", "-c", "jq --color-output . '$executionOutputFile' > '$jsonFormattedOutputExecutionFile'").inheritIO().start().waitFor()
            logger.lifecycle(jsonFormattedOutputExecutionFile.readText().replaceIndent("  "))
        }
    }

    tasks.register("invokeDeployed-$fn") {
        group = AWS_LAMBDA_GROUP
        description = "Deploys and invokes the lambda function $fn."
        dependsOn(deploy)
        finalizedBy(invokeWithAws)
    }
}

tasks.build {
    finalizedBy(tasks.shadowJar)
}
