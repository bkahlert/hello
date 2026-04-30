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
                testTask(Action {
                    applyDefaultLoggingOptions()
                    useKarma {
                        useFirefoxHeadless()
                    }
                })
            }
            yarn.applyDefaultOptions()
            binaries.executable()
        }
    }
}

// Merge `src/jsMain/resources` files of dependency klibs into this app's
// processedResources, so library-provided static assets (e.g. images) are
// served alongside index.html. Without this, only the app's own resources
// reach the distribution.
tasks.named<Copy>("jsProcessResources") {
    val jsRuntimeClasspath = configurations.named("jsRuntimeClasspath")
    inputs.files(jsRuntimeClasspath)
    from({
        jsRuntimeClasspath.get().filter { it.name.endsWith(".klib") }.map { zipTree(it) }
    }) {
        exclude("META-INF/**", "default/**", "package.json", "**/*.kotlin_module")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
