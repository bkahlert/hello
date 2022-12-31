package com.bkahlert.aws.cdk

import software.amazon.awscdk.Environment
import software.amazon.awscdk.StackProps

private fun resolveOrNull(name: String, value: String?): String? {
    val uppercaseName = name.uppercase()
    return value ?: System.getenv("CDK_DEPLOY_$uppercaseName") ?: System.getenv("CDK_DEFAULT_$uppercaseName")
}

private fun resolveOrThrow(name: String, value: String?): String =
    checkNotNull(resolveOrNull(name, value)) { "missing $name" }

/**
 * Sets the [Environment] with the specified [account] and [region].
 *
 * If [account] is missing, the following environment variables are resolved in order until a value is found:
 * - `CDK_DEPLOY_ACCOUNT` (recommend for production use)
 * - `CDK_DEFAULT_ACCOUNT` (determined by the AWS CDK command-line tool at the time of synthesis)
 *
 * If [region] is missing, the following environment variables are resolved in order until a value is found:
 * - `CDK_DEPLOY_REGION` (recommend for production use)
 * - `CDK_DEFAULT_REGION` (determined by the AWS CDK command-line tool at the time of synthesis)
 */
fun StackProps.Builder.requiredEnv(
    account: String? = null,
    region: String? = null,
): StackProps.Builder = env(
    Environment.builder()
        .account(resolveOrThrow("account", account))
        .region(resolveOrThrow("region", region))
        .build()
)

/**
 * Sets the [Environment] with the specified [account] and [region].
 *
 * If [account] is missing, the following environment variables are resolved in order until a value is found:
 * - `CDK_DEPLOY_ACCOUNT` (recommend for production use)
 * - `CDK_DEFAULT_ACCOUNT` (determined by the AWS CDK command-line tool at the time of synthesis)
 *
 * If [region] is missing, the following environment variables are resolved in order until a value is found:
 * - `CDK_DEPLOY_REGION` (recommend for production use)
 * - `CDK_DEFAULT_REGION` (determined by the AWS CDK command-line tool at the time of synthesis)
 */
fun StackProps.Builder.optionalEnv(
    account: String? = null,
    region: String? = null,
): StackProps.Builder = env(Environment.builder().run {
    resolveOrNull("account", account)?.also { account(it) }
    resolveOrNull("region", region)?.also { region(it) }
    build()
})
