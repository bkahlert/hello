plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        val jsMain by getting {
            dependencies {
                implementation("dev.fritz2:headless") { because("custom web components") }
            }
        }

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            languageSettings.optIn("kotlinx.coroutines.FlowPreview")
        }
    }
}
