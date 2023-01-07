import org.jetbrains.kotlin.cli.common.repl.replRemoveLineBreaksInTheEnd
import java.io.ByteArrayOutputStream
import java.util.Base64

plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation(project(":base"))
    implementation("io.ktor:ktor-client-auth")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-serialization")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    testImplementation(project(":base-test"))
}


/* AWS */

val AWS_GROUP = "aws"
val AWS_LAMBDA_GROUP = "aws λ"
fun Exec.aws(vararg args: String): Exec =
    commandLine("aws", *args)

fun Exec.awsLambda(vararg args: String): Exec =
    aws("lambda", *args)


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
