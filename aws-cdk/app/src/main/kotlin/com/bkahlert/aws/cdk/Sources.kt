package com.bkahlert.aws.cdk

import software.amazon.awscdk.services.s3.assets.AssetOptions
import software.amazon.awscdk.services.s3.deployment.ISource
import software.amazon.awscdk.services.s3.deployment.Source
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Uses this local asset as the deployment source.
 *
 * @see [Source.asset]
 */
fun Path.toSource(options: AssetOptions? = null): ISource =
    Source.asset(pathString, options)
