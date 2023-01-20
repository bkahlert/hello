import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {
    targets {
        js(IR) {
            browser {
                testTask {
                    useKarma {
                        useFirefoxHeadless()
                    }
                }
            }
            yarn.ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
            binaries.executable()
        }
    }
}

tasks {
    val jsBrowserDevelopmentRun = named("jsBrowserDevelopmentRun")
    val jsDevelopmentExecutableCompileSync = named("jsDevelopmentExecutableCompileSync")
    // Fix Gradle warning "Execution optimizations have been disabled"
    jsBrowserDevelopmentRun.configure { dependsOn(jsDevelopmentExecutableCompileSync) }
}
