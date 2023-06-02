plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.fritz2-project")
    id("com.bkahlert.tailwindcss-project")
    id("com.bkahlert.aws.app")
}

group = "$group.hello"

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                implementation("com.bkahlert.hello:hello-app")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
//
//val xp = tasks.register<Copy>("copyTestResources") {
//    dependsOn(tasks.jsProcessResources)
//    copy {
//        from(tasks.jsProcessResources)
//        into(tasks.jsTestProcessResources)
//    }
//}
//
//tasks.jsTestProcessResources { dependsOn(xp) }
