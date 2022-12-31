import org.jetbrains.kotlin.cli.common.repl.replRemoveLineBreaksInTheEnd
import java.io.ByteArrayOutputStream
import java.util.Base64

plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core")
    implementation("com.amazonaws:aws-lambda-java-events")
    implementation("com.amazonaws:aws-lambda-java-log4j2")

    implementation("aws.sdk.kotlin:dynamodb")
    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")

    implementation("com.auth0:java-jwt:4.2.1")
    implementation("com.auth0:jwks-rsa:0.21.2")

    implementation("com.bkahlert.kommons:kommons")

    testImplementation("com.bkahlert.kommons:kommons-test")
    testImplementation("com.amazonaws:aws-lambda-java-tests")
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

val functionToEventsMapping: Map<String, List<File>> = sourceSets.map {
    it.resources.matching { include("**/events/*/*.json") }.groupBy { eventFile -> eventFile.parentFile.name }
}.fold(mapOf()) { acc: Map<String, List<File>>, map: Map<String, List<File>> ->
    acc.keys.plus(map.keys).associateWith { key -> setOfNotNull(acc[key], map[key]).flatten() }
}


functionToEventsMapping.forEach { (fn, eventFiles) ->

    listOf<File?>(null).plus(eventFiles).forEach { eventFile ->
        val name = eventFile?.nameWithoutExtension

        tasks.register<Exec>("invoke-$fn${name?.let { "+$it" } ?: ""}") {
            group = "$AWS_LAMBDA_GROUP $fn"
            description = "Invokes the lambda function $fn${name?.let { " with event $it" } ?: ""}."
            val tmp: File? = null
            val executionOutputFile = File.createTempFile("out.", ".$fn", tmp)
            commandLine(
                "aws", "lambda", "invoke",
                "--function-name", fn,
                "--cli-binary-format", "raw-in-base64-out",
                *eventFile?.let { arrayOf("--payload", "file://$eventFile") } ?: emptyArray(),
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
    }
}

tasks.build {
    finalizedBy(tasks.shadowJar)
}
