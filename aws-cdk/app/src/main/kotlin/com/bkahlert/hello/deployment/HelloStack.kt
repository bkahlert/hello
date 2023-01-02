package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.domain
import com.bkahlert.aws.cdk.export
import com.bkahlert.aws.cdk.getContext
import com.bkahlert.aws.cdk.path
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
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
import software.amazon.awscdk.services.secretsmanager.Secret
import software.amazon.awscdk.services.secretsmanager.SecretAttributes
import software.constructs.Construct

class HelloStack(
    scope: Construct,
    id: String,
    val siteBucketDeploymentSource: ISource,
    props: StackProps? = null,
) : Stack(scope, id, props) {

    init {
        tags.setTag("stage", "dev")
    }

    /* Site bucket */
    val zoneName: String = getContext("com.bkahlert.hello.zone-name")
    val zone: IHostedZone = HostedZone.fromLookup(this, "Zone", HostedZoneProviderProps.builder().domainName(zoneName).build())

    val subDomainName: String = getContext("com.bkahlert.hello.sub-domain-name")
    val siteDomain = "$subDomainName.$zoneName"
    val siteUrl = "https://$siteDomain".export("SiteUrl", "URL of the site domain")
    val siteBucket = Bucket.Builder.create(this, "SiteBucket")
        .bucketName(siteDomain)
        .websiteIndexDocument("index.html")
        .websiteErrorDocument("index.html")
        .publicReadAccess(true)
        .removalPolicy(RemovalPolicy.DESTROY)
        .autoDeleteObjects(true)
        .build()
        .export("SiteBucketName", "Name of the site bucket") { it.bucketName }


    /* Cognito */

    val userPoolProvider = UserPoolProvider(
        scope = this,
        id = "UserPoolProvider",
        name = siteDomain,
        domainPrefix = getContext("com.bkahlert.hello.cognito.domain-prefix"),
        callbackUrl = siteUrl,
        signInWithAppleSecret = Secret.fromSecretAttributes(
            this,
            "SignInWithAppleSecret",
            SecretAttributes.builder().secretCompleteArn(getContext("com.bkahlert.hello.cognito.ip.sign-in-with-apple")).build()
        ),
    )
        .export("UserPoolProviderUrl", "URL of the user pool provider") { it.userPool.userPoolProviderUrl }
        .export("UserPoolClientId", "ID of the user pool client") { it.userPoolClient.userPoolClientId }


    /* UserProps Table + API */
    val userProps = UserProps(this, "UserProps", userPoolProvider.userPool)
        .export("UserPropsApiExecuteUrl", "URL of the UserProps execute API") { it.api.url }

    /* UserInfo API */
    val userInfo = UserInfo(this, "UserInfo", userPoolProvider.userPool, userPoolProvider.userPoolClient.userPoolClientId)
        .export("UserInfoApiExecuteUrl", "URL of the UserInfo execute API") { it.api.url }

    /* ClickUp API */
    val clickUp = ClickUp(this, "ClickUp", userPoolProvider.userPool)
        .export("ClickUpApiExecuteUrl", "URL of the ClickUp execute API") { it.api.url }

    // CloudWatch role must be specified only once, see https://docs.aws.amazon.com/cdk/api/v2/docs/aws-cdk-lib.aws_apigateway-readme.html#deployments
    // TODO separate cloud watch role; https://docs.aws.amazon.com/cdk/api/v1/docs/aws-logs-readme.html
//    val cloudWatchRole = Cloudwatch


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

    // APIs
    val apiPathRewrite = Function.Builder.create(this, "api-path-rewrite").code(
        FunctionCode.fromInline(
            // language=javascript
            """
                function handler(event) {
                    var request = event.request;
                    request.uri = request.uri.replace(/^(?:\/[^/?]*){2}/, "");
                    if(!request.uri.startsWith("/")) {
                        request.uri = "/" + request.uri   
                    }
                    return request;
                }
            """.trimIndent()
        )
    ).build()

    init {
        mapOf(
            userProps.api.url to "/api/props*",
            userInfo.api.url to "/api/info*",
            clickUp.api.url to "/api/clickup*",
        ).forEach { (url, pathPattern) ->
            cloudFrontOriginConfigs.add(
                SourceConfiguration.builder()
                    .customOriginSource(
                        CustomOriginConfig.builder()
                            .domainName(url.domain)
                            .originPath(url.path)
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
                                            .function(apiPathRewrite)
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


    /* Route53 alias record for the CloudFront distribution */

    val siteAliasRecord = ARecord.Builder.create(this, "SiteAliasRecord")
        .recordName(siteDomain)
        .target(RecordTarget.fromAlias(CloudFrontTarget(distribution)))
        .zone(zone)
        .build()


    /* Site deployment to S3 bucket */

    val siteBucketDeployment = BucketDeployment.Builder.create(this, "SiteBucketDeployment")
        .sources(listOf(siteBucketDeploymentSource))
        .destinationBucket(siteBucket)
        .distribution(distribution)
        // .distributionPaths() TODO exclude /api
        .build()
}
