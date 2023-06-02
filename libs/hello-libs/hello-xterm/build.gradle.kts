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
                implementation("com.bkahlert.kommons:kommons-js") { because("ConsoleLogger") }

                api(npm("xterm", "^5.1.0"))
                api(npm("xterm-addon-fit", "^0.7.0"))
            }
        }
    }
}
