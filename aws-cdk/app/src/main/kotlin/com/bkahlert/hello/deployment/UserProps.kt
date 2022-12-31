package com.bkahlert.hello.deployment

import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy.DESTROY
import software.amazon.awscdk.services.apigateway.AuthorizationType
import software.amazon.awscdk.services.apigateway.CognitoUserPoolsAuthorizer
import software.amazon.awscdk.services.apigateway.CorsOptions
import software.amazon.awscdk.services.apigateway.LambdaIntegration
import software.amazon.awscdk.services.apigateway.MethodOptions
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType.STRING
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.dynamodb.TableProps
import software.amazon.awscdk.services.lambda.Architecture
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.FunctionProps
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.logs.RetentionDays
import software.constructs.Construct

class UserProps(
    /** The scope in which to define this construct. */
    scope: Construct,
    /** The scoped construct ID. */
    id: String,
    /** Used for authorization. */
    userPool: UserPool,
) : Construct(scope, id) {

    /** The DynamoDB table storing used. */
    val table = Table(
        this, "Table", TableProps.builder()
            .partitionKey(Attribute.builder().name("userId").type(STRING).build())
            .sortKey(Attribute.builder().name("propId").type(STRING).build())
            .removalPolicy(DESTROY)
            .build()
    )


    val functionEnvironment: Map<String, String> = mapOf(
        "TABLE_NAME" to table.tableName,
        "PARTITION_KEY" to table.schema().partitionKey.name,
        "SORT_KEY" to checkNotNull(table.schema().sortKey) { "sort key missing" }.name,
    )

    val packageName = "com.bkahlert.hello.props"

    val getAllFunction = Function(this, "GetAllFunction", FunctionProps(functionEnvironment, "$packageName.GetAllHandler"))
        .also { table.grantReadData(it) }
    val getOneFunction = Function(this, "GetOneFunction", FunctionProps(functionEnvironment, "$packageName.GetOneHandler"))
        .also { table.grantReadData(it) }
    val createOneFunction = Function(this, "CreateOneFunction", FunctionProps(functionEnvironment, "$packageName.CreateOneHandler"))
        .also { table.grantReadWriteData(it) }
    val updateOneFunction = Function(this, "UpdateOneFunction", FunctionProps(functionEnvironment, "$packageName.UpdateOneHandler"))
        .also { table.grantReadWriteData(it) }
    val deleteOneFunction = Function(this, "DeleteOneFunction", FunctionProps(functionEnvironment, "$packageName.DeleteOneHandler"))
        .also { table.grantReadWriteData(it) }

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
        .cloudWatchRole(true)
        .defaultCorsPreflightOptions(
            CorsOptions.builder()
                .allowCredentials(false)
                .allowHeaders(listOf("Content-Type", "X-Amz-Date", "Authorization", "X-Api-Key", "X-Amz-Security-Token", "X-Amz-User-Agent"))
                .allowMethods(listOf("OPTIONS", "GET", "PUT", "POST", "PATCH", "DELETE"))
                .allowOrigins(listOf("*"))
                .build()
        )
        .build()

    init {
        api.root.apply {
            addMethod("GET", LambdaIntegration(getAllFunction), methodOptions)
            addMethod("POST", LambdaIntegration(createOneFunction), methodOptions)
            addResource("{id}").apply {
                addMethod("GET", LambdaIntegration(getOneFunction), methodOptions)
                addMethod("PATCH", LambdaIntegration(updateOneFunction), methodOptions)
                addMethod("DELETE", LambdaIntegration(deleteOneFunction), methodOptions)
            }
        }
    }

    private fun FunctionProps(environment: Map<String, String>, handler: String): FunctionProps {
        return FunctionProps.builder()
            .code(Code.fromAsset("../../aws-lambdas/props-api-handlers/build/libs/props-api-handlers-all.jar"))
            .handler(handler)
            .architecture(Architecture.ARM_64)
            .runtime(Runtime.JAVA_11)
            .environment(environment)
            .timeout(Duration.seconds(30))
            .memorySize(1024)
            .logRetention(RetentionDays.FIVE_DAYS)
            .build()
    }
}
