plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation(project(":base"))
    implementation("aws.sdk.kotlin:dynamodb")

    testImplementation(project(":base-test"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")

    // TODO remove
    implementation("com.bkahlert.kommons:kommons-debug")
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
