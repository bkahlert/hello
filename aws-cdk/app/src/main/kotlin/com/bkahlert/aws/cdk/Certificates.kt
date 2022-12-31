package com.bkahlert.aws.cdk

import software.amazon.awscdk.customresources.AwsCustomResource
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy
import software.amazon.awscdk.customresources.AwsCustomResourceProps
import software.amazon.awscdk.customresources.AwsSdkCall
import software.amazon.awscdk.customresources.PhysicalResourceId
import software.amazon.awscdk.customresources.PhysicalResourceIdReference
import software.amazon.awscdk.customresources.SdkCallsPolicyOptions
import software.amazon.awscdk.services.certificatemanager.Certificate
import software.amazon.awscdk.services.certificatemanager.ICertificate
import software.constructs.Construct

/**
 * Creates [AwsCustomResource] to request a certificate for the specified [domainName]
 * and optional [subjectAlternativeNames].
 *
 * The request can also be made with the AWS command-line interface:
 * ```shell
 * aws acm request-certificate \
 *   --domain-name "foo.example.com" \
 *   --subject-alternative-names '["bar.example.com", "baz.example.com"]' \
 *   --validation-method "DNS"
 * ```
 */
fun requestCertificate(
    scope: Construct,
    domainName: String,
    vararg subjectAlternativeNames: String,
): ICertificate {
    val idSuffix = listOf(domainName, *subjectAlternativeNames).joinToString("")
    val validatedCertificateRequest = AwsCustomResource(
        scope, "RequestValidatedAcmCertificate$idSuffix", AwsCustomResourceProps.builder()
            .onCreate(
                AwsSdkCall.builder()
                    .service("ACM")
                    .action("requestCertificate")
                    .parameters(
                        mapOf(
                            "DomainName" to domainName,
                            "SubjectAlternativeNames" to subjectAlternativeNames.asList(),
                            "ValidationMethod" to "DNS",
                        )
                    )
                    .physicalResourceId(PhysicalResourceId.fromResponse("CertificateArn"))
                    .build()
            )
            .onDelete(
                AwsSdkCall.builder()
                    .service("ACM")
                    .action("deleteCertificate")
                    .parameters(
                        mapOf(
                            "CertificateArn" to PhysicalResourceIdReference(),
                        )
                    )
                    .build()
            )
            .policy(
                AwsCustomResourcePolicy.fromSdkCalls(
                    SdkCallsPolicyOptions.builder()
                        .resources(AwsCustomResourcePolicy.ANY_RESOURCE)
                        .build()
                )
            ).build()
    )

    /*
        TODO currently not working
        Stack event with logical ID SiteDistributionCFDistribution209CF7F5:
        Resource handler returned message:
            Invalid request provided: The specified SSL certificate doesn't exist, isn't in us-east-1 region,
                                      isn't valid, or doesn't include a valid certificate chain.
                                      (Service: CloudFront, Status Code: 400, Request ID: 20b40125-5c2d-4e13-a98c-f9a97075d1bb)
        (RequestToken: b60ebe9e-5fc0-0a89-4628-081918cf12c8, HandlerErrorCode: InvalidRequest)
     */
    return Certificate.fromCertificateArn(scope, "Certificate", validatedCertificateRequest.getResponseField("CertificateArn"))
}
