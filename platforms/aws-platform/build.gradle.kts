plugins {
    id("java-platform")
}

group = "com.bkahlert.platform"

// Enable the definition of dependencies to other platforms like the Spring Boot BOM
javaPlatform.allowDependencies()

dependencies {

    // AWS CDK
    api(platform("software.amazon.awssdk:bom:2.19.4"))
    constraints {
        api("software.amazon.awscdk:aws-cdk-lib:2.56.1")
        api("software.constructs:constructs:10.1.200")
    }

    // AWS SDK for Java (BOM)
    api(platform("software.amazon.awssdk:bom:2.19.4"))

    // AWS SDK for Kotlin (BOM version does not exist)
    constraints {
        val awsSdkKotlinVersion = "0.19.2-beta"
        api("aws.sdk.kotlin:dynamodb:$awsSdkKotlinVersion")
        api("aws.sdk.kotlin:lambda:$awsSdkKotlinVersion")
        api("aws.sdk.kotlin:s3:$awsSdkKotlinVersion")
        api("aws.sdk.kotlin:secretsmanager:$awsSdkKotlinVersion")
    }

    // AWS Lambda support for Java
    constraints {
        api("com.amazonaws:aws-lambda-java-core:1.2.2")
        api("com.amazonaws:aws-lambda-java-events:3.11.0")
        api("com.amazonaws:aws-lambda-java-log4j2:1.5.1")
        api("com.amazonaws:aws-lambda-java-tests:1.1.1")
    }

    // Misc
    constraints {
        api("ch.qos.logback:logback-classic:1.4.5")
        api("org.apache.logging.log4j:log4j-api:2.19.0")
        api("org.apache.logging.log4j:log4j-core:2.19.0")
        api("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")
    }
}
