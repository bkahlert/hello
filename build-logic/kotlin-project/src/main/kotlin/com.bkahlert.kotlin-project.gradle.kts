import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

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

tasks.withType(KotlinCompilationTask::class).configureEach {
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    filter {
        isFailOnNoMatchingTests = false
    }
}
