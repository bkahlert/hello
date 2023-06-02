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
                api(npm("socket.io-client", "^4.6.1"))
            }
        }
    }
}
