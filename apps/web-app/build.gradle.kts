import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

// set environment variable ORG_GRADLE_PROJECT_isProduction to true on CI

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
                        cssSupport { enabled.set(true) }
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
                implementation("com.bkahlert.kommons:kommons")
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.kommons:kommons-util")
                implementation("com.bkahlert.semantic-ui:semantic-ui")

                implementation("com.bkahlert.hello:clickup-model")
                implementation("com.bkahlert.hello:clickup-viewmodel")
                implementation("com.bkahlert.hello:clickup-view")
                implementation("com.bkahlert.hello:clickup-http-client")
                implementation("com.bkahlert.hello:hello-client")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
