import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.bkahlert.kotlin-project") apply false
}

kotlin {
    jvmToolchain(11)
    targets {
        jvm()
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.set(freeCompilerArgs.get() + "-Xjsr305=strict")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
