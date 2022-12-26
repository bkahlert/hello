import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
}

kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project.dependencies.platform("com.bkahlert.platform:product-platform"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project.dependencies.platform("com.bkahlert.platform:test-platform"))
                implementation("com.bkahlert.kommons:kommons-test")
            }
        }

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
        jvmTarget = "1.8"
        apiVersion = "1.7"
        languageVersion = "1.7"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    filter {
        isFailOnNoMatchingTests = false
    }
}
