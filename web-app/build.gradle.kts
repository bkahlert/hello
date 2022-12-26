import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("com.bkahlert.compose-web-application")
}

group = "$group.hello"

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
        }
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons")
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-deployment")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-web")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-auth")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-js")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-serialization")
                implementation("io.ktor:ktor-serialization-kotlinx-json")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}

val jsBrowserProductionWebpack = tasks.named<KotlinWebpack>("jsBrowserProductionWebpack")
