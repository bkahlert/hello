import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.bkahlert.commons")
    kotlin("multiplatform")
}

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation("com.bkahlert.kommons:kommons-test:2.5.0")
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.experimental.ExperimentalTypeInference")
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "1.8"
        apiVersion = "1.7"
        languageVersion = "1.7"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    filter {
        isFailOnNoMatchingTests = false
    }
}
