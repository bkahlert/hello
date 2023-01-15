import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("com.bkahlert.kotlin-project") apply false
    id("org.jetbrains.compose") apply false
}

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(compose.web.testUtils)
            }

            languageSettings.optIn("org.jetbrains.compose.web.testutils.ComposeWebExperimentalTestsApi")
        }

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
        }
    }
}

tasks.withType(KotlinCompilationTask::class).configureEach {
    compilerOptions {
        freeCompilerArgs.set(
            freeCompilerArgs.get() + listOf(
                "-P",
                "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.8.0"
            )
        )
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0-alpha02")
}
