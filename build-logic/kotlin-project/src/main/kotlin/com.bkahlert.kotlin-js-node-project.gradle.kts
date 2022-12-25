import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    id("com.bkahlert.kotlin-project")
}

kotlin {
    targets {
        js(IR) {
            nodejs {
                testTask {
                    useMocha {
                        timeout = "10000"
                    }
                }
            }
            yarn.ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
        }
    }
}
