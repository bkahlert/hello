import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.bkahlert.compose-web-application")
    alias(libs.plugins.kotlin.plugin.serialization)
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
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kommons)
                implementation("com.bkahlert.kommons:kommons-ktor")
                implementation("com.bkahlert.kommons:kommons-web")
            }

            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}

val jsBrowserProductionWebpack = tasks.named<KotlinWebpack>("jsBrowserProductionWebpack")
