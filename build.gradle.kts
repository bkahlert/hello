import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    id("org.hidetake.ssh")
}

group = "com.bkahlert"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser {
            runTask {
                sourceMaps = true
            }
            testTask {
                testLogging.showStandardStreams = true
                useKarma {
                    useChromeHeadless()
                    useFirefox()
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation("com.bkahlert.kommons:kommons:1.11.5")

                // https://github.com/JetBrains/kotlin-wrappers
//                fun kotlinWrapper(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"
//                val kotlinWrappersVersion = "1.0.1-pre.290-kotlin-1.6.10"
//                implementation(project.dependencies.platform(kotlinWrapper("wrappers-bom:${kotlinWrappersVersion}")))
//                implementation(kotlinWrapper("extensions")) { because("require") }
//                implementation(npm("jsHue", ">= 2.1.1"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-opt-in=org.jetbrains.compose.web.ExperimentalComposeWebApi"
}

val deploy by tasks.registering {
    dependsOn(tasks.named("jsBrowserDistribution"))
    doLast {
        ssh.runSessions {
            session(vaults["ssh-remotes.yml", "remotes", "default"]) {
                put(buildDir.resolve("distributions").listFiles(), "./")
            }
        }
    }
}
