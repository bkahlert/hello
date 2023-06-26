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
                api(project(":hello-page"))
                api(project(":hello-showcase"))
                api(project(":hello-widgets"))
                api("com.bkahlert.kommons:kommons-color")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
