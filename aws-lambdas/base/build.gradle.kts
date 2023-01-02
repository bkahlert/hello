plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    api("com.amazonaws:aws-lambda-java-core") { because("RequestHandler interface") }
    api("com.amazonaws:aws-lambda-java-events") { because("Event classes, e.g. APIGatewayProxyRequestEvent") }
    api("com.amazonaws:aws-lambda-java-log4j2") { because("Log4J") }
    api("org.apache.logging.log4j:log4j-slf4j18-impl") { because("Log4J based TestLogger using SLF4J") }
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core") { because("suspend function enabled RequestHandler adapter") }
    api("org.jetbrains.kotlinx:kotlinx-serialization-json") { because("Utilities for easy JSON serialization") }
    testImplementation(project(":base-test"))
}
