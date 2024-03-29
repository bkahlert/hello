package com.bkahlert.aws

import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.provideDelegate
import kotlin.reflect.KProperty

/**
 * Executes the specified [command] and its [args]
 * and returns its output on success.
 * Otherwise, throws an [IllegalStateException].
 */
private fun exec(@Suppress("SameParameterValue") command: String, vararg args: String): String {
    val commandLine = listOf(command, *args)
    val process = ProcessBuilder(commandLine).start()
    val exitCode = process.waitFor()
    val outBytes = process.inputStream.readBytes()
    val out = String(outBytes).trim()
    val errBytes = process.errorStream.readBytes()
    return if (exitCode == 0) {
        out
    } else {
        val err = String(errBytes)
        throw IllegalStateException("Failed to run $commandLine:\nOUTPUT:\n$out\nERROR:\n$err")
    }
}

/**
 * A command line tool that used [command] as
 * its entry point.
 */
class CommandLineTool(
    /** Command name of the command line tool. */
    internal val command: String,
) {
    /** The absolute path to the binary of this command line tool. */
    private val binary: String by lazy { com.bkahlert.aws.exec("which", command) }

    /** Returns the command line to invoke this tool with the specified [args]. */
    fun commandLine(args: List<String>): List<String> =
        mutableListOf(binary).apply { addAll(args) }

    /** Returns the command line to invoke this tool with the specified [args]. */
    fun commandLine(vararg args: String): List<String> =
        commandLine(args.asList())

    fun exec(vararg args: String): String =
        com.bkahlert.aws.exec(binary, *args)
}

/**
 * Utility to build [Exec.commandLine] in the specified [exec] for the specified [tool].
 */
class CommandLineToolExec(
    private val exec: Exec,
    private val tool: CommandLineTool,
) {
    /** Sets the command line to this [tool] and the specified [args]. */
    operator fun invoke(args: List<String>): Exec =
        runCatching { exec.commandLine(tool.commandLine(args)) }
            .getOrElse {
                exec.logger.error("Failed to set command line to ${tool.command} $args", it)
                exec.enabled = false
                exec
            }

    /** Sets the command line to this [tool] and the specified [args]. */
    operator fun invoke(vararg args: String): Exec =
        invoke(args.asList())
}

private operator fun CommandLineTool.getValue(thisRef: Exec, property: KProperty<*>): CommandLineToolExec =
    CommandLineToolExec(thisRef, this)

/** [AWS Command Line Interface](https://aws.amazon.com/cli/) */
val AWS: CommandLineTool = CommandLineTool("aws")

/** [AWS Command Line Interface](https://aws.amazon.com/cli/) [Exec] task integration */
val Exec.aws: CommandLineToolExec by AWS

/** [AWS CDK Toolkit](https://docs.aws.amazon.com/cdk/v2/guide/cli.html) */
val CDK: CommandLineTool = CommandLineTool("cdk")

/** [AWS CDK Toolkit](https://docs.aws.amazon.com/cdk/v2/guide/cli.html) [Exec] task integration */
val Exec.cdk: CommandLineToolExec by CDK


/** [jq Command Line Interface](https://stedolan.github.io/jq/) */
val JQ: CommandLineTool = CommandLineTool("jq")

/** [jq Command Line Interface](https://stedolan.github.io/jq/) [Exec] task integration */
val Exec.jq: CommandLineToolExec by JQ
