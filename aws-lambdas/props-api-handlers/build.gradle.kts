plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core")
    implementation("com.amazonaws:aws-lambda-java-events")
    implementation("com.amazonaws:aws-lambda-java-log4j2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
    implementation("aws.sdk.kotlin:dynamodb")

    // TODO remove
    implementation("com.bkahlert.kommons:kommons-debug:2.5.0")

    testImplementation("com.amazonaws:aws-lambda-java-tests")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
