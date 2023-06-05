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
                api(project(":hello-widgets"))
                api(project(":hello-widgets-image"))
                api(project(":hello-widgets-feature-preview"))
                api(project(":hello-widgets-video"))
                api(project(":hello-widgets-ssh"))
                api(project(":hello-widgets-website"))
                api(project(":hello-bookmarks"))
                api(project(":hello-chatbot"))
                api(project(":hello-components"))
                api(project(":hello-data-view"))
                api(project(":hello-editor"))
                api(project(":hello-quick-links"))
                api(project(":hello-showcase"))
                api(project(":hello-toaster"))
                api("com.bkahlert.kommons:kommons-core")
                api("com.bkahlert.kommons:kommons-color")
                api("com.bkahlert.kommons:kommons-dom")
                api(npm("encrypt-storage", "^2.12")) { because("localStorage encryption for offline props copy") }
                api("com.bkahlert.kommons:kommons-net") { because("LenientJson, JsonHttpClient") }
                api("com.bkahlert.kommons:kommons-uri")

                api("net.pearx.kasechange:kasechange") { because("toCase extension function") }
            }
        }
    }
}
