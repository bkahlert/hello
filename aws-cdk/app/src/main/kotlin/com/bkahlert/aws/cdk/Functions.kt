package com.bkahlert.aws.cdk

import software.amazon.awscdk.services.apigateway.Cors
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.lambda.FunctionProps

/**
 * Returns a map suitable to be passed to [FunctionProps.Builder.environment]
 * reflecting the [CorsOptions].
 */
fun CorsOptions.toEnvironment(
    filterAllowHeaders: (String) -> Boolean = { true },
    filterAllowMethods: (String) -> Boolean = { true },
): Map<String, String> = buildMap {
    allowCredentials?.also { put("ACCESS_CONTROL_ALLOW_CREDENTIALS", it.toString()) }
    put("ACCESS_CONTROL_ALLOW_HEADERS", (allowHeaders ?: Cors.DEFAULT_HEADERS).filter(filterAllowHeaders).joinToString(","))
    allowMethods?.filter(filterAllowMethods)?.also { put("ACCESS_CONTROL_ALLOW_METHODS", it.joinToString(",")) }
    put("ACCESS_CONTROL_ALLOW_ORIGIN", allowOrigins.joinToString(","))
}
