plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {
    targets {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
    }
}
