plugins {
    id("com.bkahlert.kotlin-js-library")
    id("com.bkahlert.kotlin-serialization-feature")
}

group = "$group.kommons"

kotlin {
    sourceSets {
        val gen by creating
        val jsMain by getting {
            dependsOn(gen)

            dependencies {
                implementation(libs.kommons)
            }
        }
    }
}

tasks {
    @Suppress("UNUSED_VARIABLE")
    val generateUnicodeData by registering(Exec::class) {
        group = "build"

        commandLine("bash", "build-config.sh", "src/gen/kotlin/com/bkahlert/kommons/deployment/gen/info.kt")
        doLast {
//            val dir = projectDir.resolve("src/nativeMain/kotlin/com/bkahlert/kommons/text")
//            val generated = Unicode.UnicodeData.generate(dir.resolve("UnicodeData.kt"))

//            logger.lifecycle("Generated $generated")
        }
    }

    assemble.configure {
        dependsOn(generateUnicodeData)
    }
}
