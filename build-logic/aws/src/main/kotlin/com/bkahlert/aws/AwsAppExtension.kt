package com.bkahlert.aws

import org.gradle.api.provider.Property

interface AwsAppExtension {
    val environmentFile: Property<String>
}
