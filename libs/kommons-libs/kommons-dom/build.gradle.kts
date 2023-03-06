plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                api(project(":kommons-js"))
                api("com.bkahlert.kommons:kommons-uri")
            }
        }
    }
}
