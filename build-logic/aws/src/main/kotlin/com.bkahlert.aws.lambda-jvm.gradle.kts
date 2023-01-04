import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("jvm") apply false
    kotlin("plugin.serialization") apply false
    id("com.github.johnrengelman.shadow")
}

java { sourceCompatibility = JavaVersion.toVersion("11") }

dependencies {
    implementation(platform("com.bkahlert.platform:aws-platform"))
    implementation(platform("com.bkahlert.platform:product-platform"))

    testImplementation(platform("com.bkahlert.platform:test-platform"))
    testImplementation("com.bkahlert.kommons:kommons-test")
    testImplementation("com.amazonaws:aws-lambda-java-tests")
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
        jvmTarget = "11"
        apiVersion = "1.7"
        languageVersion = "1.7"
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
