plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.compose-for-web-project")
}

group = "$group.hello"

kotlin {
    explicitApi()
}
