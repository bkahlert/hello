plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-core")
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-json-ktor")
                implementation("com.bkahlert.kommons:kommons-logging-inline")

                implementation(project(":hello-dom")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":hello-compose")) { because("ClickUpHttpClientConfigurer") }

                implementation(project(":semantic-ui-core")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":semantic-ui-elements")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":semantic-ui-collections")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":semantic-ui-modules")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":semantic-ui-views")) { because("ClickUpHttpClientConfigurer") }
                implementation(project(":semantic-ui-custom")) { because("ClickUpHttpClientConfigurer") }

                implementation(project(":clickup-model"))
                implementation(project(":clickup-view")) { because("ClickUpHttpClientConfigurer") }
            }
        }

        val jsTest by getting {
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
