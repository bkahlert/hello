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
                implementation(compose.runtime)
                implementation(compose.web.core)
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
    val kotlinVersion = "1.8.20-Beta"
    val composePlugin = "plugin:androidx.compose.compiler.plugins.kotlin"
    compilerOptions {
        if (kotlinVersion.split(".").last().split("-").first().toIntOrNull() != 20) {
            freeCompilerArgs.set(freeCompilerArgs.get() + listOf("-P", "$composePlugin:suppressKotlinVersionCompatibilityCheck=$kotlinVersion"))
        }
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.3-dev-k1.8.20-Beta-c5841510cbf")
}
