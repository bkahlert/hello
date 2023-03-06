plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-dom")
                api("com.bkahlert.kommons:kommons-net")
                api(project(":clickup-model"))

                implementation("com.bkahlert.semantic-ui:semantic-ui") { because("ClickUpHttpClientConfigurer") }
                implementation(project(":clickup-view")) { because("ClickUpHttpClientConfigurer") }
            }
        }

        jsTest {
            dependencies {
                implementation("io.ktor:ktor-client-js")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
