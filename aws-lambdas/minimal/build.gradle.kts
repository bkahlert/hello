plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core")

    // more type-safety for +1 MB
    // implementation("com.amazonaws:aws-lambda-java-events")

    // Log4j logging for +2 MB
    // implementation("com.amazonaws:aws-lambda-java-log4j2")
}
