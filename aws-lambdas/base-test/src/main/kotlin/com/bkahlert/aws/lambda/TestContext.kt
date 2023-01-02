package com.bkahlert.aws.lambda

import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger

/**
 * A context for testing purposes
 * that exposes logged message using [log].
 */
public class TestContext(
    private val awsRequestId: String = "495b12a8-xmpl-4eca-8168-160484189f99",
    private val logGroupName: String = "/aws/lambda/my-function",
    private val logStreamName: String = "2020/02/26/[\$LATEST]704f8dxmpla04097b9134246b8438f1a",
    private val functionName: String = "my-function",
    private val functionVersion: String = "\$LATEST",
    private val invokedFunctionArn: String = "arn:aws:lambda:us-east-2:123456789012:function:my-function",
    private val identity: CognitoIdentity? = null,
    private val clientContext: ClientContext? = null,
    private val remainingTimeInMillis: Int = 300000,
    private val memoryLimitInMB: Int = 512,
) : Context {
    private val _log = mutableListOf<String>()
    private val logger: LambdaLogger = TestLogger(_log)

    /** Contains all logged messages. */
    public val log: List<String> = _log

    override fun getAwsRequestId(): String = awsRequestId
    override fun getLogGroupName(): String = logGroupName
    override fun getLogStreamName(): String = logStreamName
    override fun getFunctionName(): String = functionName
    override fun getFunctionVersion(): String = functionVersion
    override fun getInvokedFunctionArn(): String = invokedFunctionArn
    override fun getIdentity(): CognitoIdentity? = identity
    override fun getClientContext(): ClientContext? = clientContext
    override fun getRemainingTimeInMillis(): Int = remainingTimeInMillis
    override fun getMemoryLimitInMB(): Int = memoryLimitInMB
    override fun getLogger(): LambdaLogger = logger
}
