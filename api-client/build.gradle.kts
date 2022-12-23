plugins {
    kotlin("multiplatform") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
    id("org.jetbrains.compose") version "1.3.0-rc01"
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
                implementation("com.bkahlert.kommons:kommons-web")
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


compose {
    kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:${libs.versions.compose.compiler.get()}")
}
