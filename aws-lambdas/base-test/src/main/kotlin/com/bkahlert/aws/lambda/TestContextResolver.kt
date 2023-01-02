package com.bkahlert.aws.lambda

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver

public class TestContextResolver : TypeBasedParameterResolver<TestContext>() {
    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): TestContext =
        TestContext()
}
