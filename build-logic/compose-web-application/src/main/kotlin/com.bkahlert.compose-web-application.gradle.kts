plugins {
    id("com.bkahlert.kotlin-js-browser-project") apply false
    id("org.jetbrains.compose") apply false
}

kotlin {
    targets {
        js(IR) {
            binaries.executable()
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
            }

            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                progressiveMode = true // false by default
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(compose.web.testUtils)
            }

            languageSettings.apply {
                optIn("org.jetbrains.compose.web.testutils.ComposeWebExperimentalTestsApi")
            }
        }
    }
}

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0-alpha02")
}

tasks {
    val jsBrowserDevelopmentRun = named("jsBrowserDevelopmentRun")
    val jsDevelopmentExecutableCompileSync = named("jsDevelopmentExecutableCompileSync")
    // Fix Gradle warning "Execution optimizations have been disabled"
    jsBrowserDevelopmentRun.configure { dependsOn(jsDevelopmentExecutableCompileSync) }
}
