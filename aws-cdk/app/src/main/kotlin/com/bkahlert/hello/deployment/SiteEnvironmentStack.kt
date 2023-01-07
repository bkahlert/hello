package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.export
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.IntegrationResponse
import software.amazon.awscdk.services.apigateway.MethodOptions
import software.amazon.awscdk.services.apigateway.MethodResponse
import software.amazon.awscdk.services.apigateway.MockIntegration
import software.amazon.awscdk.services.apigateway.PassthroughBehavior
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.apigateway.StageOptions
import software.constructs.Construct

class SiteEnvironmentStack(
    /** The parent of this stack. */
    parent: Construct? = null,
    /** The scoped construct ID. */
    id: String? = null,
    /** The stack properties. */
    props: StackProps? = null,
    environment: Map<String, String>,
) : Stack(parent, id, props) {

    val api = RestApi.Builder.create(this, "RestApi")
        .restApiName("SiteEnvironment API")
        .deployOptions(StageOptions.builder().tracingEnabled(true).build())
        .build()
        .export("SiteEnvironmentApiEndpoint", "URL of the API") { it.url }

    init {
        api.root.apply {
            addMethod(
                "GET", MockIntegration.Builder.create()
                    .integrationResponses(
                        listOf(
                            IntegrationResponse.builder()
                                .statusCode("200")
                                .responseTemplates(
                                    mapOf(
                                        "application/json" to toJsonString(environment),
                                    )
                                )
                                .build()
                        )
                    )
                    .passthroughBehavior(PassthroughBehavior.NEVER)
                    .requestTemplates(
                        mapOf(
                            "application/json" to "{ \"statusCode\": 200 }",
                        )
                    )
                    .build(),
                MethodOptions.builder()
                    .methodResponses(listOf(MethodResponse.builder().statusCode("200").build()))
                    .build()
            )
        }
    }
}
