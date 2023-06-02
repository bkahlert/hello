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
                api("com.bkahlert.kommons:kommons-uri")
            }
        }
    }
}
