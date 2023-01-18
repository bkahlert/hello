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
    val composePlugin = "plugin:androidx.compose.compiler.plugins.kotlin"
    compilerOptions {
        freeCompilerArgs.set(freeCompilerArgs.get() + listOf("-P", "$composePlugin:suppressKotlinVersionCompatibilityCheck=1.8.0"))
//        freeCompilerArgs.set(freeCompilerArgs.get() + listOf("-P", "$composePlugin:liveLiterals=false"))
//        freeCompilerArgs.set(freeCompilerArgs.get() + listOf("-P", "$composePlugin:liveLiteralsEnabled=false"))
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0-alpha02")
}
