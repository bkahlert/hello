plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

kotlin {
    explicitApi()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core")
    implementation("com.amazonaws:aws-lambda-java-events")

    implementation("com.bkahlert.kommons:kommons-debug")

    api(platform("com.bkahlert.platform:test-platform"))

    api("com.bkahlert.kommons:kommons-test") { because("Basic JUnit setup (in particular service location)") }
    api("com.amazonaws:aws-lambda-java-tests") { because("@Event annotation") }
    api("org.apache.logging.log4j:log4j-api") { because("Log4J based TestLogger") }
    api("org.apache.logging.log4j:log4j-core") { because("Log4J based TestLogger") }
    api("org.apache.logging.log4j:log4j-slf4j18-impl") { because("Log4J based TestLogger using SLF4J") }
}
