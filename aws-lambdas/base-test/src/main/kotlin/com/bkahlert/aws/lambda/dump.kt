package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.bkahlert.kommons.debug.Compression.Never
import com.bkahlert.kommons.debug.CustomToString
import com.bkahlert.kommons.debug.render

public fun LambdaLogger.dump(
    event: APIGatewayProxyRequestEvent,
    context: Context,
) {
    val eventDump = event.dump()
    val contextDump = context.dump()
    log("EVENT:\n$eventDump\nCONTEXT:\n$contextDump")
}

private fun <T> T.dump(): String =
    render {
        compression = Never
        customToString = CustomToString.Ignore
        filterProperties { _, prop -> prop != "serialVersionUID" && prop != "logger" }
    }
