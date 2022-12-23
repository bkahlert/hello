import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("com.bkahlert.compose-web-application")
    kotlin("plugin.serialization") version "1.7.21"
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
        val jsMain by getting {
            dependencies {
                implementation(libs.bundles.ktor.js.client)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kommons)
                implementation("com.bkahlert.kommons:kommons-web")
            }
        }
    }
}

val jsBrowserProductionWebpack = tasks.named<KotlinWebpack>("jsBrowserProductionWebpack")
