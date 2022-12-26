package com.bkahlert.aws

import com.fasterxml.jackson.module.kotlin.readValue

import java.lang.ProcessBuilder

object CloudFormation {

    fun listExports(query: String? = null, output: String? = "json"): String {
        val cmdLine = mutableListOf<String>().apply {
            add("aws")
            add("cloudformation")
            add("list-exports")
            add("--no-paginate")
            if (output != null) {
                add("--output")
                add(output)
            }
            if (query != null) {
                add("--query")
                add(query)
            }
        }
        val proc = ProcessBuilder(cmdLine).start()
        proc.waitFor()
        val outBytes = proc.inputStream.readAllBytes()
        val errBytes = proc.errorStream.readAllBytes()
        val err = String(errBytes)
        if (err.isEmpty()) {
            return String(outBytes)
        } else {
            throw IllegalStateException("Failed to run $cmdLine: $err")
        }
    }

    fun listConfig(service: String, stage: String, removePrefix: Boolean = true): Map<String, String?> {
        val prefix = "sls-$service-$stage-"
        val json = listExports(query = "Exports[?contains(Name,`$prefix`)].[Name, Value]")
        val list: List<List<String>> = mapper.readValue(json)
        val map: Map<String, String?> = list.associate {
            val key = if (removePrefix) it[0].removePrefix(prefix) else it[0]
            key to it[1]
        }
        return map
    }
}
