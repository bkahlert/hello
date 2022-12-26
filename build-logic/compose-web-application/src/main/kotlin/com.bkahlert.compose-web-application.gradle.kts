import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    id("com.bkahlert.kotlin-project") apply false
    id("org.jetbrains.compose") apply false
}

kotlin {
    targets {
        js(IR) {
            browser {
                testTask {
                    useKarma {
//                        useChromeHeadless()
                        useFirefoxHeadless()
                    }
                }
            }
            yarn.ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
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

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.4.0-alpha02")
}

tasks {
    val jsBrowserDevelopmentRun = named("jsBrowserDevelopmentRun")
    val jsDevelopmentExecutableCompileSync = named("jsDevelopmentExecutableCompileSync")
    // Fix Gradle warning "Execution optimizations have been disabled"
    jsBrowserDevelopmentRun.configure { dependsOn(jsDevelopmentExecutableCompileSync) }
}
