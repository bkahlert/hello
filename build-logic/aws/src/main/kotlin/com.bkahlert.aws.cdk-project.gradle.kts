import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("jvm") apply false
}

dependencies {
    implementation(platform("com.bkahlert.platform:aws-platform"))
    implementation("software.amazon.awscdk:aws-cdk-lib")
    implementation("software.constructs:constructs")

    implementation(platform("com.bkahlert.platform:product-platform"))

    testImplementation(platform("com.bkahlert.platform:test-platform"))
    testImplementation("com.bkahlert.kommons:kommons-test")
}

kotlin {
    jvmToolchain(11)
    with(javaToolchains.launcherFor(java.toolchain).get().metadata) { logger.info("Using JDK $languageVersion toolchain installed in $installationPath") }

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

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(freeCompilerArgs.get() + "-Xjsr305=strict")
        freeCompilerArgs.set(freeCompilerArgs.get() + "-Xcontext-receivers")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
