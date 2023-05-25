package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.export
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.certificatemanager.Certificate
import software.constructs.Construct

class CertificateStack(
    /** The parent of this stack. */
    parent: Construct? = null,
    /** The scoped construct ID. */
    id: String? = null,
    /** The stack properties. */
    props: StackProps? = null,
    siteDomain: String,
) : Stack(parent, id, props) {

    val siteCertificate = Certificate.Builder.create(this, "SiteCertificate")
        .domainName(siteDomain)
        .build()
        .export("SiteCertificateArn", "ARN of the site certificate") { it.certificateArn }
}
