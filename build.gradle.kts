import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
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
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                when (mode) {
                    PRODUCTION -> {
                        devtool = null
                    }
                    DEVELOPMENT -> {
                        devtool = "eval-source-map"
                        cssSupport.enabled = true
                        // main config in webpack.config.d directory
                    }
                }
            }
            testTask {
                testLogging.showStandardStreams = true
                useKarma {
                    useChromeHeadless()
//                    useFirefox()
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
                @Suppress("LocalVariableName") val ktor_version = "2.0.0-beta-1"
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-js:$ktor_version")
                implementation("io.ktor:ktor-client-auth:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                implementation("io.ktor:ktor-client-serialization:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
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
                implementation("io.kotest:kotest-assertions-core-js:5.1.0")
            }
        }
    }
}

tasks.withType<Kotlin2JsCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.contracts.ExperimentalContracts"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    kotlinOptions.freeCompilerArgs += "-opt-in=org.jetbrains.compose.web.ExperimentalComposeWebApi"
}
tasks.withType<Test> {
    useJUnitPlatform()
}

val deploy by tasks.registering {
    dependsOn(tasks.named("jsBrowserProductionWebpack"))
    doLast {
        ssh.runSessions {
            session(vaults["ssh-remotes.yml", "remotes", "default"]) {
                put(buildDir.resolve("distributions").listFiles(), "./")
            }
        }
    }
}
