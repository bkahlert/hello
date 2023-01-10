package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.domain
import com.bkahlert.aws.cdk.export
import com.bkahlert.aws.cdk.path
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.apigateway.RestApi
import software.amazon.awscdk.services.certificatemanager.DnsValidatedCertificate
import software.amazon.awscdk.services.cloudfront.Behavior
import software.amazon.awscdk.services.cloudfront.CfnDistribution
import software.amazon.awscdk.services.cloudfront.CloudFrontAllowedCachedMethods
import software.amazon.awscdk.services.cloudfront.CloudFrontAllowedMethods
import software.amazon.awscdk.services.cloudfront.CloudFrontWebDistribution
import software.amazon.awscdk.services.cloudfront.CustomOriginConfig
import software.amazon.awscdk.services.cloudfront.Function
import software.amazon.awscdk.services.cloudfront.FunctionAssociation
import software.amazon.awscdk.services.cloudfront.FunctionCode
import software.amazon.awscdk.services.cloudfront.FunctionEventType.VIEWER_REQUEST
import software.amazon.awscdk.services.cloudfront.OriginProtocolPolicy
import software.amazon.awscdk.services.cloudfront.OriginSslPolicy
import software.amazon.awscdk.services.cloudfront.PriceClass
import software.amazon.awscdk.services.cloudfront.ResponseHeadersCorsBehavior
import software.amazon.awscdk.services.cloudfront.ResponseHeadersPolicy
import software.amazon.awscdk.services.cloudfront.S3OriginConfig
import software.amazon.awscdk.services.cloudfront.SSLMethod
import software.amazon.awscdk.services.cloudfront.SecurityPolicyProtocol.TLS_V1_2_2021
import software.amazon.awscdk.services.cloudfront.SourceConfiguration
import software.amazon.awscdk.services.cloudfront.ViewerCertificate
import software.amazon.awscdk.services.cloudfront.ViewerCertificateOptions
import software.amazon.awscdk.services.route53.ARecord
import software.amazon.awscdk.services.route53.HostedZone
import software.amazon.awscdk.services.route53.HostedZoneProviderProps
import software.amazon.awscdk.services.route53.IHostedZone
import software.amazon.awscdk.services.route53.RecordTarget
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.deployment.BucketDeployment
import software.amazon.awscdk.services.s3.deployment.ISource
import software.constructs.Construct

class DistributionStack(
    /** The parent of this stack. */
    parent: Construct? = null,
    /** The scoped construct ID. */
    id: String? = null,
    /** The stack properties. */
    props: StackProps? = null,
    val zoneName: String,
    val siteDomain: String,
    val siteUrl: String,
    val apis: Map<RestApi, String>,
    val sources: List<ISource>,
    val distributionsPaths: List<String>,
) : Stack(parent, id, props) {

    init {
        tags.setTag("stage", "dev")
    }

    /* Site bucket */
    val zone: IHostedZone = HostedZone.fromLookup(this, "Zone", HostedZoneProviderProps.builder().domainName(zoneName).build())

    init {
        siteUrl.export("SiteUrl", "URL of the site domain")
    }

    val siteBucket = Bucket.Builder.create(this, "SiteBucket")
        .bucketName(siteDomain)
        .websiteIndexDocument("index.html")
        .websiteErrorDocument("index.html")
        .publicReadAccess(true)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .build()
        .export("SiteBucketName", "Name of the site bucket") { it.bucketName }


    /* Certificate */

    val siteCertificate = DnsValidatedCertificate.Builder.create(this, "SiteCertificate")
        .domainName(siteDomain)
        .hostedZone(zone)
        .build()
        .export("SiteCertificateArn", "ARN of the site certificate") { it.certificateArn }


    /* CloudFront distribution */
    val cloudFrontOriginConfigs = mutableListOf<SourceConfiguration>()

    // SiteBucket
    val indexHtmlDefaultPathRewrite = Function.Builder.create(this, "index-html-default-path-rewrite").code(
        FunctionCode.fromInline(
            // language=javascript
            """
                function handler(event) {
                    var request = event.request;
                    if(request.uri === "" || request.uri === "/") {
                        request.uri = "/index.html";
                    }
                    return request;
                }
            """.trimIndent()
        )
    ).build()

    // S3
    init {
        cloudFrontOriginConfigs.add(
            SourceConfiguration.builder()
                .s3OriginSource(S3OriginConfig.builder().s3BucketSource(siteBucket).build())
                .behaviors(
                    listOf(
                        Behavior.builder()
                            .isDefaultBehavior(true)
                            .functionAssociations(
                                listOf(
                                    FunctionAssociation.builder()
                                        .eventType(VIEWER_REQUEST)
                                        .function(indexHtmlDefaultPathRewrite)
                                        .build()
                                )
                            )
                            .build()
                    )
                )
                .build()
        )
    }

    // Rewrite functions
    val apiPathRewrites = buildMap<Int, Function> {
        // TODO refactor so that the ClickUp-Proxy, which needs a nesting 1 function
        // does not rely on it being implicitly generated because of the
        // Environment API
        apis.values.map { pathPattern -> pathPattern.nesting }.distinct().forEach { nesting ->
            put(
                nesting, Function.Builder.create(this@DistributionStack, "api-path-rewrite-$nesting").code(
                    FunctionCode.fromInline(
                        // language=javascript
                        """
                        function handler(event) {
                            var request = event.request;
                            request.uri = request.uri.replace(/^(?:\/[^/?]*){$nesting}/, "");
                            if(!request.uri.startsWith("/")) {
                                request.uri = "/" + request.uri   
                            }
                            return request;
                        }
                    """.trimIndent()
                    )
                ).build()
            )
        }
    }

    // ClickUp-Proxy
    val clickUpProxyResponseHeaderPolicy = ResponseHeadersPolicy.Builder.create(this, "ClickUpProxyResponseHeaderPolicy")
        .responseHeadersPolicyName("ClickUp-Proxy")
        .corsBehavior(
            ResponseHeadersCorsBehavior.builder()
                .accessControlAllowOrigins(
                    listOf(
                        "http://localhost:8080",
                        "http://localhost:8081",
                        "https://hello.aws-dev.choam.de",
                    )
                )
                .accessControlAllowCredentials(true)
                .accessControlAllowHeaders(listOf("Authorization", "Content-Type"))
                .accessControlAllowMethods(listOf("GET", "HEAD", "PUT", "POST", "PATCH", "DELETE", "OPTIONS"))
                .accessControlExposeHeaders(listOf("Authorization", "Content-Type"))
                .accessControlMaxAge(Duration.seconds(600))
                .originOverride(true)
                .build()
        )
        .build()

    init {
        cloudFrontOriginConfigs.add(
            SourceConfiguration.builder()
                .customOriginSource(
                    CustomOriginConfig.builder()
                        .domainName("api.clickup.com")
                        .allowedOriginSslVersions(listOf(OriginSslPolicy.TLS_V1_2))
                        .originProtocolPolicy(OriginProtocolPolicy.HTTPS_ONLY)
                        .build()
                ).behaviors(
                    listOf(
                        Behavior.builder()
                            .forwardedValues(
                                CfnDistribution.ForwardedValuesProperty.builder()
                                    .cookies(CfnDistribution.CookiesProperty.builder().forward("all").build())
                                    .headers(listOf("Authorization"))
                                    .queryString(true)
                                    .build()
                            )
                            .allowedMethods(CloudFrontAllowedMethods.ALL)
                            .cachedMethods(CloudFrontAllowedCachedMethods.GET_HEAD)
                            .isDefaultBehavior(false)
                            .defaultTtl(Duration.minutes(0))
                            .maxTtl(Duration.minutes(0))
                            .minTtl(Duration.minutes(0))
                            .pathPattern("/api.clickup.com*")
                            .functionAssociations(
                                listOf(
                                    FunctionAssociation.builder()
                                        .eventType(VIEWER_REQUEST)
                                        .function(apiPathRewrites[1])
                                        .build()
                                )
                            )
                            .build()
                    )
                ).build()
        )
    }

    // APIs
    init {
        apis.forEach { (restApi, pathPattern) ->
            cloudFrontOriginConfigs.add(
                SourceConfiguration.builder()
                    .customOriginSource(
                        CustomOriginConfig.builder()
                            .domainName(restApi.url.domain)
                            .originPath(restApi.url.path)
                            .allowedOriginSslVersions(listOf(OriginSslPolicy.TLS_V1_2))
                            .originProtocolPolicy(OriginProtocolPolicy.HTTPS_ONLY)
                            .build()
                    ).behaviors(
                        listOf(
                            Behavior.builder()
                                .forwardedValues(
                                    CfnDistribution.ForwardedValuesProperty.builder()
                                        .cookies(CfnDistribution.CookiesProperty.builder().forward("all").build())
                                        .headers(listOf("Authorization"))
                                        .queryString(true)
                                        .build()
                                )
                                .allowedMethods(CloudFrontAllowedMethods.ALL)
                                .cachedMethods(CloudFrontAllowedCachedMethods.GET_HEAD)
                                .isDefaultBehavior(false)
                                .defaultTtl(Duration.minutes(0))
                                .maxTtl(Duration.minutes(0))
                                .minTtl(Duration.minutes(0))
                                .pathPattern(pathPattern)
                                .functionAssociations(
                                    listOf(
                                        FunctionAssociation.builder()
                                            .eventType(VIEWER_REQUEST)
                                            .function(apiPathRewrites[pathPattern.nesting])
                                            .build()
                                    )
                                )
                                .build()
                        )
                    ).build()
            )
        }
    }

    val distribution = CloudFrontWebDistribution.Builder.create(this, "Distribution")
        .viewerCertificate(
            ViewerCertificate.fromAcmCertificate(
                siteCertificate,
                ViewerCertificateOptions.builder()
                    .aliases(listOf(siteDomain))
                    .sslMethod(SSLMethod.SNI)
                    .securityPolicy(TLS_V1_2_2021)
                    .build()
            )
        )
        .defaultRootObject("")
        .originConfigs(cloudFrontOriginConfigs)
        .priceClass(PriceClass.PRICE_CLASS_100)
        .build()
        .export("DistributionId", "ID of the distribution") { it.distributionId }
        .export("DistributionUrl", "URL of the distribution") { "https://${it.distributionDomainName}" }

    init {
        // Super annoying ... as of 2023-01-10
        // this seems to be the only way to assign a response header policy
        // to a behaviour.
        val cfnDistribution = distribution.node.defaultChild as CfnDistribution
        cfnDistribution.addOverride(
            "Properties.DistributionConfig.CacheBehaviors.0.ResponseHeadersPolicyId",
            clickUpProxyResponseHeaderPolicy.responseHeadersPolicyId,
        )
    }


    /* Route53 alias record for the CloudFront distribution */

    val siteAliasRecord = ARecord.Builder.create(this, "SiteAliasRecord")
        .recordName(siteDomain)
        .target(RecordTarget.fromAlias(CloudFrontTarget(distribution)))
        .zone(zone)
        .build()


    /* Site deployment to S3 bucket */

    val siteBucketDeployment = BucketDeployment.Builder.create(this, "SiteBucketDeployment")
        .sources(sources)
        .destinationBucket(siteBucket)
        .distribution(distribution)
        .distributionPaths(distributionsPaths)
        .build()

    private val String.nesting get() = split('/').size - 1
}
