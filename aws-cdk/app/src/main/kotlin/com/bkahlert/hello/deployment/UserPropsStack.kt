package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.enableCorsOnGatewayResponses
import com.bkahlert.aws.cdk.export
import com.bkahlert.aws.cdk.toEnvironment
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy.DESTROY
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.AuthorizationType
import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizer
import software.amazon.awscdk.services.apigateway.Cors
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.apigateway.MethodOptions
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.apigateway.StageOptions
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType.STRING
import software.amazon.awscdk.services.dynamodb.BillingMode.PAY_PER_REQUEST
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.lambda.Architecture
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.FunctionProps
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.lambda.Tracing
import software.amazon.awscdk.services.logs.RetentionDays
import software.constructs.Construct


class UserPropsStack(
    /** The parent of this stack. */
    parent: Construct? = null,
    /** The scoped construct ID. */
    id: String? = null,
    /** The stack properties. */
    props: StackProps? = null,
    /** Code that implements the API. */
    code: Code,
    /** Used for authorization. */
    userPool: UserPool,
) : Stack(parent, id, props) {

    /** The DynamoDB table storing used. */
    val table = Table.Builder.create(this, "Table")
        .partitionKey(Attribute.builder().name("userId").type(STRING).build())
        .sortKey(Attribute.builder().name("propId").type(STRING).build())
        .billingMode(PAY_PER_REQUEST)
        .removalPolicy(DESTROY)
        .build()

    val functionEnvironment: Map<String, String> = mapOf(
        "TABLE_NAME" to table.tableName,
        "PARTITION_KEY" to table.schema().partitionKey.name,
        "SORT_KEY" to checkNotNull(table.schema().sortKey) { "sort key missing" }.name,
    )

    val corsOptions = CorsOptions.builder()
        .allowCredentials(false)
        .allowHeaders(Cors.DEFAULT_HEADERS)
        .allowMethods(Cors.ALL_METHODS)
        .allowOrigins(Cors.ALL_ORIGINS)
        .build()

    val packageName = "com.bkahlert.hello.user.props"

    val getAllFunction = Function(
        this,
        "GetAllFunction",
        FunctionProps(functionEnvironment + corsOptions.toEnvironment(), code, "$packageName.GetAllHandler")
    ).also { table.grantReadData(it) }

    val getOneFunction = Function(
        this,
        "GetOneFunction",
        FunctionProps(functionEnvironment + corsOptions.toEnvironment(), code, "$packageName.GetOneHandler")
    ).also { table.grantReadData(it) }

    val createOneFunction =
        Function(
            this,
            "CreateOneFunction",
            FunctionProps(functionEnvironment + corsOptions.toEnvironment(), code, "$packageName.CreateOneHandler")
        ).also { table.grantReadWriteData(it) }

    val updateOneFunction = Function(
        this,
        "UpdateOneFunction",
        FunctionProps(functionEnvironment + corsOptions.toEnvironment(), code, "$packageName.UpdateOneHandler")
    ).also { table.grantReadWriteData(it) }

    val deleteOneFunction = Function(
        this,
        "DeleteOneFunction",
        FunctionProps(functionEnvironment + corsOptions.toEnvironment(), code, "$packageName.DeleteOneHandler")
    ).also { table.grantReadWriteData(it) }

    val authorizer = CognitoUserPoolsAuthorizer.Builder.create(this, "Authorizer")
        .cognitoUserPools(listOf(userPool))
        .build()

    private val methodOptions = MethodOptions.builder()
        .authorizer(authorizer)
        .authorizationType(AuthorizationType.COGNITO)
        .authorizationScopes(listOf("openid"))
        .build()

    val api = RestApi.Builder.create(this, "RestApi")
        .restApiName("UserProps API")
        .deployOptions(StageOptions.builder().tracingEnabled(true).build())
        .build()
        .apply { enableCorsOnGatewayResponses(corsOptions) }
        .export("UserPropsApiEndpoint", "URL of the API") { it.url }

    init {
        api.root.apply {
            addCorsPreflight(corsOptions)
            addMethod("GET", LambdaIntegration(getAllFunction), methodOptions)
            addMethod("POST", LambdaIntegration(createOneFunction), methodOptions)
            addResource("{id}").apply {
                addCorsPreflight(corsOptions)
                addMethod("GET", LambdaIntegration(getOneFunction), methodOptions)
                addMethod("POST", LambdaIntegration(createOneFunction), methodOptions)
                addMethod("PATCH", LambdaIntegration(updateOneFunction), methodOptions)
                addMethod("DELETE", LambdaIntegration(deleteOneFunction), methodOptions)
            }
        }
    }

    private fun FunctionProps(environment: Map<String, String>, code: Code, handler: String): FunctionProps {
        return FunctionProps.builder()
            .code(code)
            .handler(handler)
            .architecture(Architecture.ARM_64)
            .runtime(Runtime.JAVA_11)
            .environment(environment)
            .timeout(Duration.seconds(30))
            .memorySize(1024)
            .logRetention(RetentionDays.FIVE_DAYS)
            .tracing(Tracing.ACTIVE)
            .build()
    }
}
