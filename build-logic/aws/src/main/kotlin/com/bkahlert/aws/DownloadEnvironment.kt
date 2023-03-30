package com.bkahlert.aws

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class DownloadEnvironment : DefaultTask() {

    @get:OutputFile
    abstract val environmentFile: RegularFileProperty

    /**
     * Downloads the environment from AWS CloudFormation
     * using the following script:
     * ```bash
     * aws cloudformation list-exports \
     *   --no-paginate \
     *   --output json \
     *   --query 'Exports[*].{Name:Name, Value:Value}' \
     *   | jq '
     *   def camel_to_snake:
     *     [
     *       splits("(?=[A-Z])")
     *     ]
     *     | map( select(. != "") | ascii_upcase )
     *     | join("_")
     *     ;
     *   map({ (.Name): .Value })
     *   | add
     *   | with_entries( select(.key | contains(":") | not ) )
     *   | with_entries( .key |= camel_to_snake )
     *   ' > environment.json
     * ```
     */
    @TaskAction
    fun download() {
        val exports = listExports()
        val formattedExports = formatExports(exports)
        environmentFile.get().asFile.writeText(formattedExports)
    }

    /**
     * Lists all exports of the current AWS account in the form:
     * ```json
     * [ { "Name": "export-name", "Value": "export-value" }, ... ]
     * ```
     */
    private fun listExports() = AWS.exec(
        "cloudformation", "list-exports",
        "--no-paginate",
        "--output", "json",
        "--query", "Exports[*].{Name:Name, Value:Value}",
    )

    /**
     * Formats the specified [exports] to as JSON of the form:
     * ```json
     * {
     *   "EXPORT_NAME": "export-value",
     *   ...
     * }
     * ```
     */
    private fun formatExports(exports: String): String {
        val jqInput = temporaryDir.resolve("exports.json").apply { writeText(exports) }
        return JQ.exec(
            """
                def camel_to_snake:
                  [
                    splits("(?=[A-Z])")
                  ]
                  | map( select(. != "") | ascii_upcase )
                  | join("_")
                  ;
                map({ (.Name): .Value })
                | add
                | with_entries( select(.key | contains(":") | not ) )
                | with_entries( .key |= camel_to_snake )
            """.trimIndent(),
            jqInput.absolutePath,
        )
    }
}
