import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.webpack.WebpackDevtool

plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project")
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
                implementation("com.bkahlert.kommons:kommons-auth")
                implementation("com.bkahlert.kommons:kommons-auth-ktor")
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-json")

                implementation("com.bkahlert.hello:hello-color")
                implementation("com.bkahlert.hello:hello-url")
                implementation("com.bkahlert.hello:hello-dom")
                implementation("com.bkahlert.hello:hello-compose")
                implementation("com.bkahlert.hello:semantic-ui-core")
                implementation("com.bkahlert.hello:semantic-ui-elements")
                implementation("com.bkahlert.hello:semantic-ui-collections")
                implementation("com.bkahlert.hello:semantic-ui-modules")
                implementation("com.bkahlert.hello:semantic-ui-views")
                implementation("com.bkahlert.hello:semantic-ui-custom")
                implementation("com.bkahlert.hello:clickup-model")
                implementation("com.bkahlert.hello:clickup-viewmodel")
                implementation("com.bkahlert.hello:clickup-view")
                implementation("com.bkahlert.hello:hello-client")

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
