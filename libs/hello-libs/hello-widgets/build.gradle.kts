plugins {
    id("com.bkahlert.kotlin-js-browser-project")
    id("com.bkahlert.fritz2-project")
}

group = "$group.hello"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                api(project(":hello-fritz2"))
                api(project(":hello-button"))
                api(project(":hello-editor"))
                api(project(":hello-metadata"))
                api("com.bkahlert.kommons:kommons-core")
                api("com.bkahlert.kommons:kommons-color")
                api("com.bkahlert.kommons:kommons-dom")
                api(npm("encrypt-storage", "^2.12")) { because("localStorage encryption for offline props copy") }
                api("com.bkahlert.kommons:kommons-net") { because("LenientJson, JsonHttpClient") }
                api("com.bkahlert.kommons:kommons-uri")

                api("net.pearx.kasechange:kasechange") { because("toCase extension function") }
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
