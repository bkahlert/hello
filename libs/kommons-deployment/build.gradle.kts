import com.bkahlert.aws.CloudFormation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant

plugins {
    id("com.bkahlert.kotlin-js-project")
    id("com.bkahlert.aws")
}

group = "$group.kommons"

kotlin {
    explicitApi()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val gen by creating
        val commonMain by getting {
            dependsOn(gen)
        }
    }
}
/*
public data class Config(
    public val openIDProvider: String,
    public val clientId: String,
    public val apiDomain: String,
) {
    public companion object {
        public val DEFAULT: Config = Config(
            openIDProvider = "https://cognito-idp.eu-central-1.amazonaws.com/eu-central-1_2kcGMqneE",
            clientId = "7lhdbv12q1ud9rgg7g779u8va7",
            apiDomain = "api.hello-dev.aws.choam.de",
        )
    }
}

 */
tasks {
    val generateClientConfig by registering {
        group = "build"

        doLast {
            val config = CloudFormation.listConfig("hello", "dev")
            val jacksonWriter = jacksonObjectMapper()

            val clientConfig = mapOf(
                "openIdProvider" to config["OpenIDProvider"],
                "clientId" to config["OAuth2ClientID"],
                "apiDomain" to config["DomainNameHttp"],
            )
            val kotlinSources: File = projectDir.resolve("src/gen/kotlin")
            val packageName = "com.bkahlert.hello.client"

            val dir: File = kotlinSources.resolve(packageName.replace('.', '/')).also { it.mkdirs() }
            val file: File = dir.resolve("Config.kt")

            file.also { if (it.exists()) it.delete() }.writer().use {
                it.write("package $packageName\n")
                it.write("\n")
                it.write("/** Client configuration created ${Instant.now()} */\n")
                it.write("@Suppress(\"SpellCheckingInspection\")\n")
                it.write("public data class Config(\n")
                clientConfig.forEach { (key, value) ->
                    it.write("    public val $key: String,")
                    it.write("\n")
                }
                it.write(") {\n")
                it.write("    public companion object {\n")
                it.write("        public val DEFAULT: Config = Config(\n")
                clientConfig.forEach { (key, value) ->
                    it.write("            $key = ")
                    it.write(jacksonWriter.writeValueAsString(value))
                    it.write(",\n")
                }
                it.write("        )\n")
                it.write("    }\n")
                it.write("}\n")
            }

            logger.lifecycle("Client configuration created at {}", file)
        }
    }

    assemble.configure {
        dependsOn(generateClientConfig)
    }
}
