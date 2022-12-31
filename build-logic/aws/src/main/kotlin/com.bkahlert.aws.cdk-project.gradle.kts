import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("jvm") apply false
}

java { sourceCompatibility = JavaVersion.toVersion("11") }

dependencies {
    implementation(platform("com.bkahlert.platform:aws-platform"))
    implementation("software.amazon.awscdk:aws-cdk-lib")
    implementation("software.constructs:constructs")

    implementation(platform("com.bkahlert.platform:product-platform"))

    testImplementation(platform("com.bkahlert.platform:test-platform"))
    testImplementation("com.bkahlert.kommons:kommons-test")
}

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            languageSettings.progressiveMode = true // false by default
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        freeCompilerArgs += "-Xcontext-receivers" // context receivers / multiple receivers
        jvmTarget = "11"
        apiVersion = "1.7"
        languageVersion = "1.7"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
