package com.bkahlert.hello.api.user

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.bkahlert.kommons.debug.Compression.Never
import com.bkahlert.kommons.debug.CustomToString
import com.bkahlert.kommons.debug.render
import org.slf4j.Logger

fun Logger.dump(
    event: APIGatewayV2HTTPEvent,
    context: Context,
) {
    debug("EVENT:\n{}\nCONTEXT:\n{}", event.dump(), context.dump())
}

private fun <T> T.dump(): String =
    render {
        compression = Never
        customToString = CustomToString.Ignore
        filterProperties { _, prop -> prop != "logger" }
    }
