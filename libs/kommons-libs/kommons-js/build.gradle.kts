import com.bkahlert.kotlinWrapper

plugins {
    id("com.bkahlert.kotlin-js-project")
}

group = "$group.kommons"

kotlin {
    explicitApi()
    sourceSets {
        jsMain {
            dependencies {
                implementation(kotlinWrapper("extensions"))
                implementation(kotlinWrapper("js"))
                implementation(npm("debug", "^4.3.4"))
            }
        }
    }
}
