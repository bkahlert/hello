plugins {
    kotlin("multiplatform") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0"
}

group = "com.bkahlert.hello"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser {
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
                implementation("com.bkahlert.hello.model:kommons-web")
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation(libs.bundles.ktor.js.client)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(npm("amazon-cognito-identity-js", "2.0.2", generateExternals = true))
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
                implementation(kotlin("test-js"))
            }
        }
    }
}
