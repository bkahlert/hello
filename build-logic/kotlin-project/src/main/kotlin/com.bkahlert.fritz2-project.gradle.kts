plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        val jsMain by getting {
            dependencies {
                implementation("dev.fritz2:headless") { because("custom web components") }

                implementation(devNpm("postcss", "^8.4")) { because("CSS post transformation, e.g. auto-prefixing") }
                implementation(devNpm("postcss-loader", "^7.0")) { because("Loader to process CSS with PostCSS") }
                implementation(devNpm("postcss-import", "^15.1")) { because("@import support") }
                implementation(devNpm("autoprefixer", "^10.4")) { because("auto-prefixing by PostCSS") }
                implementation(devNpm("cssnano", "^5.1")) { because("CSS minification by PostCSS") }
                implementation(devNpm("tailwindcss", "^3.2")) { because("low-level CSS classes") }

                // optional tailwind plugins
                implementation(devNpm("@tailwindcss/typography", "^0.5")) { because("tailwind text formatting, i.e. from external sources") }
                implementation(devNpm("@tailwindcss/forms", "^0.5")) { because("form-specific tailwind features") }
                implementation(devNpm("@tailwindcss/line-clamp", "^0.4")) { because("tailwind text truncation based on line count") }
                implementation(devNpm("@tailwindcss/aspect-ratio", "^0.4")) { because("tailwind aspect-ratio classes") }
                implementation(devNpm("tailwind-heropatterns", "^0.0.8")) { because("hero-pattern like striped backgrounds") }

                // OK-HCL color support
                implementation(devNpm("@csstools/postcss-oklab-function", "^2.1")) { because("oklch support for PostCSS") }
                implementation(devNpm("chroma-js", "^2.4.2")) { because("oklch color manipulation in tailwind config") }
            }
        }

        all {
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}
