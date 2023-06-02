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
                api(project(":hello-icon"))
                implementation(npm("@spectrum-web-components/split-view", "^0.5")) { because("resize handle for showcase") }
            }
        }
    }
}
