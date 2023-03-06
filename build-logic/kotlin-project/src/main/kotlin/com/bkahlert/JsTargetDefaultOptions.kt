package com.bkahlert

import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

/**
 * Configures the following options:
 * - [enable lifecycle scripts](https://kotlinlang.org/docs/js-project-setup.html#installing-npm-dependencies-with-ignore-scripts-by-default)
 * - [automatic yarn.lock updates](https://kotlinlang.org/docs/js-project-setup.html#reporting-that-yarn-lock-has-been-updated)
 */
fun YarnRootExtension.applyDefaultOptions() {
    ignoreScripts = false // suppress "warning Ignored scripts due to flag." warning
    yarnLockMismatchReport = YarnLockMismatchReport.NONE
    reportNewYarnLock = true // true
    yarnLockAutoReplace = true // true
}


fun KotlinJsTest.applyDefaultLoggingOptions() {
    testLogging {
        showStandardStreams = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}


fun KotlinJsBrowserDsl.defaultWebpackConfig() {
    commonWebpackConfig {
        cssSupport { enabled.set(true) }
        scssSupport { enabled.set(true) }
        devServer = devServer?.copy(
            open = false,
        )
        progressReporter = true
        showProgress = true
    }
}
