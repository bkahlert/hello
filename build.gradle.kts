import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    id("org.hidetake.ssh")
    id("io.kotest.multiplatform") version "5.1.0"
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
            commonWebpackConfig {
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
        val commonTest by getting {
            dependencies {
                implementation("io.kotest:kotest-framework-engine:5.1.0")
                implementation("io.kotest:kotest-assertions-core:5.1.0")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
                val ktor_version = "1.6.7"
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-js:$ktor_version")
                implementation("io.ktor:ktor-client-auth:$ktor_version")
                implementation("io.ktor:ktor-client-serialization:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
//                implementation("io.ktor:ktor-server-cors:$ktor_version")
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
                implementation("io.kotest:kotest-framework-engine-js:5.1.0")
                implementation("io.kotest:kotest-assertions-core-js:5.1.0")
            }
        }
    }
}

tasks.withType<Kotlin2JsCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=org.jetbrains.compose.web.ExperimentalComposeWebApi"
}
tasks.withType<Test> {
    useJUnitPlatform()
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
