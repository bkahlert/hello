plugins {
    id("com.bkahlert.kotlin-js-browser-application")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {

    sourceSets {
        jsMain {
            dependencies {
                implementation("com.bkahlert.kommons:kommons")
                implementation("com.bkahlert.kommons:kommons-dom")
                implementation("com.bkahlert.kommons:kommons-net")
                implementation("com.bkahlert.semantic-ui:semantic-ui")
                implementation("com.bkahlert.hello:clickup")
                implementation("com.bkahlert.hello:hello")

                implementation(devNpm("less", "^4.1")) { because("dynamic stylesheet language") }
                implementation(devNpm("less-loader", "^11.1")) { because("Less to CSS compilation") }
                implementation(devNpm("postcss", "^8.4")) { because("CSS post transformation, e.g. auto-prefixing") }
                implementation(devNpm("postcss-loader", "^7.0")) { because("Loader to process CSS with PostCSS") }
                implementation(devNpm("postcss-import", "^15.1")) { because("@import support") }
                implementation(devNpm("autoprefixer", "^10.4")) { because("autoprefixing by PostCSS") }
                implementation(devNpm("cssnano", "^5.1")) { because("CSS minification by PostCSS") }
                implementation(devNpm("tailwindcss", "^3.2")) { because("low-level CSS classes") }

//                implementation("dev.petuska:kmdc:0.1.0")
//                implementation("dev.petuska:kmdcx:0.1.0")
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }
    }
}
