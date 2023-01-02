plugins {
    id("com.bkahlert.aws.lambda-jvm")
}

dependencies {
    implementation(project(":base"))
    implementation("com.auth0:java-jwt:4.2.1")
    implementation("com.auth0:jwks-rsa:0.21.2")
    implementation("com.bkahlert.kommons:kommons-core")

    testImplementation(project(":base-test"))
}
