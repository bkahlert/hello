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
                api("com.bkahlert.kommons:kommons-random")
                api(project(":hello-fritz2"))
                api(project(":hello-chat"))
                api(project(":hello-page"))
                api(project(":hello-showcase"))
                api(project(":hello-widgets"))
                implementation("com.aallam.openai:openai-client")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("com.aallam.openai.api.BetaOpenAI")
        }
    }
}
