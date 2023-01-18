import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(platform("com.bkahlert.platform:aws-platform"))
    implementation(platform("com.bkahlert.platform:product-platform"))

    testImplementation(platform("com.bkahlert.platform:test-platform"))
    testImplementation("com.bkahlert.kommons:kommons-test")
    testImplementation("com.amazonaws:aws-lambda-java-tests")
}

kotlin {
    jvmToolchain(8)
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
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}


tasks.jar {
    archiveVersion.set("")
}
tasks.shadowJar {
    archiveVersion.set("")
    mergeServiceFiles()
    transform(Log4j2PluginsCacheFileTransformer::class.java)
}
