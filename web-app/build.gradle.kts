import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.compose)
}

group = "com.bkahlert"
version = "1.0"

repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
                        cssSupport { enabled = true }
                        // main config in webpack.config.d directory
                    }
                }

                outputFileName = "hello.js"
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
                implementation(libs.bundles.ktor.js.client)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.serialization.kotlinx.json)
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

compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:${libs.versions.compose.compiler.get()}")
}

rootProject.extensions.configure<NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0" // fixes webpack-cli incompatibility by pinning the newest version
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val jsBrowserProductionWebpack = tasks.named<KotlinWebpack>("jsBrowserProductionWebpack")
