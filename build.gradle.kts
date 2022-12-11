import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    kotlin("plugin.serialization") version "1.7.0"
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev741"
    id("org.hidetake.ssh")
}

group = "com.bkahlert"
version = "1.0"

repositories {
    google()
    mavenCentral()
    mavenLocal()
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
                        devtool = WebpackDevtool.EVAL_SOURCE_MAP
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
        yarn.ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.runtime)
                @Suppress("LocalVariableName") val ktor_version = "2.0.3"
                listOf("core", "js", "auth", "logging", "serialization", "content-negotiation").forEach { api("io.ktor:ktor-client-$it:$ktor_version") }
                listOf("json").forEach { api("io.ktor:ktor-serialization-kotlinx-$it:$ktor_version") }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

                implementation(libs.kommons)
            }

            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("org.jetbrains.compose.web.ExperimentalComposeWebApi")
                progressiveMode = true // false by default
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.kommons.test)
                implementation(compose.web.testUtils)
            }

            languageSettings.apply {
                optIn("org.jetbrains.compose.web.testutils.ComposeWebExperimentalTestsApi")
            }
        }
    }
}

rootProject.plugins.withType<NodeJsRootPlugin> {
    rootProject.the<NodeJsRootExtension>().nodeVersion = "16.0.0"
}

rootProject.extensions.configure<NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0" // fixes webpack-cli incompatibility by pinning the newest version
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    val deploy by registering {
        dependsOn(named("jsBrowserProductionWebpack"))
        doLast {
            ssh.runSessions {
                session(vaults["ssh-remotes.yml", "remotes", "default"]) {
                    put(buildDir.resolve("distributions").listFiles(), "./")
                }
            }
        }
    }
}
