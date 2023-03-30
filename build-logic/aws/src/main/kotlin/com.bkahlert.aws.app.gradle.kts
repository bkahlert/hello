import com.bkahlert.aws.AwsAppExtension
import com.bkahlert.aws.DownloadEnvironment

plugins {
    id("base")
    id("com.bkahlert.commons")
}

val awsApp = extensions.create<AwsAppExtension>("awsApp")

val downloadEnvironment = tasks.register<DownloadEnvironment>("downloadEnvironment") {
//    environmentFile.convention(awsApp.environmentFile)
    val dest = layout.projectDirectory.file("environment.json")
    environmentFile.set(dest)
}

tasks.assemble {
    dependsOn(downloadEnvironment)
}
