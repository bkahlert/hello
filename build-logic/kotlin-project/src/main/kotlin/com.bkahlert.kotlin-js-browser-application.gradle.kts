import com.bkahlert.applyDefaultLoggingOptions
import com.bkahlert.applyDefaultOptions
import com.bkahlert.defaultWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn

plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {
    targets {
        js(IR) {
            useCommonJs()
            browser {
                defaultWebpackConfig()
                testTask {
                    applyDefaultLoggingOptions()
                    useKarma {
                        useFirefoxHeadless()
                    }
                }
            }
            yarn.applyDefaultOptions()
            binaries.executable()
        }
    }
}
