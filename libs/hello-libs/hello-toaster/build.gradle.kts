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
                api("com.bkahlert.kommons:kommons-error")
                api("com.bkahlert.kommons:kommons-random")
                api(project(":hello-fritz2"))
                api(project(":hello-font"))
                api(project(":hello-icon"))
                implementation("net.pearx.kasechange:kasechange") { because("toCase extension function") }
            }
        }
    }
}
