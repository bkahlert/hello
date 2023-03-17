package com.bkahlert.hello.deployment

import com.bkahlert.aws.cdk.export
import software.amazon.awscdk.Duration
import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.Stack
import software.amazon.awscdk.StackProps
import software.amazon.awscdk.services.cognito.CognitoDomainOptions
import software.amazon.awscdk.services.cognito.OAuthFlows
import software.amazon.awscdk.services.cognito.OAuthScope
import software.amazon.awscdk.services.cognito.OAuthSettings
import software.amazon.awscdk.services.cognito.SignInAliases
import software.amazon.awscdk.services.cognito.StandardAttribute
import software.amazon.awscdk.services.cognito.StandardAttributes
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.cognito.UserPoolClient
import software.amazon.awscdk.services.cognito.UserPoolClientIdentityProvider
import software.amazon.awscdk.services.cognito.UserPoolDomain
import software.amazon.awscdk.services.cognito.UserPoolIdentityProviderApple
import software.amazon.awscdk.services.secretsmanager.Secret
import software.amazon.awscdk.services.secretsmanager.SecretAttributes
import software.constructs.Construct

/**
 * Defines a Cognito-backed OpenID provider.
 */
class UserPoolProviderStack(
    /** The parent of this stack. */
    parent: Construct? = null,
    /** The scoped construct ID. */
    id: String? = null,
    /** The stack properties. */
    props: StackProps? = null,
    /** The name of the corresponding [userPool]. */
    val name: String,
    /** The domain prefix of the corresponding [userPoolDomain]. */
    val domainPrefix: String,
    /** The callback URL of the corresponding [userPoolClient]. */
    val callbackUrls: List<String>,
    /** The credentials of the corresponding [signInWithAppleIdentityProvider]. */
    val signInWithAppleSecretArn: String?,
) : Stack(parent, id, props) {

    /** The user pool for storing user information. */
    @Suppress("MemberVisibilityCanBePrivate")
    val userPool: UserPool = UserPool.Builder.create(this, "UserPool")
        .userPoolName(name)
        .selfSignUpEnabled(true)
        .signInAliases(SignInAliases.builder().email(true).username(true).build())
        .standardAttributes(
            StandardAttributes.builder()
                .email(StandardAttribute.builder().required(false).mutable(true).build())
                .nickname(StandardAttribute.builder().required(false).mutable(true).build())
                .profilePicture(StandardAttribute.builder().required(false).mutable(true).build())
                .build()
        )
        .removalPolicy(RemovalPolicy.DESTROY)
        .build()
        .export("UserPoolProviderUrl", "URL of the user pool provider") { it.userPoolProviderUrl }

    /** The user pool domain hosting features like the sign-up feature. */
    @Suppress("MemberVisibilityCanBePrivate")
    val userPoolDomain: UserPoolDomain = UserPoolDomain.Builder.create(this, "UserPoolDomain")
        .userPool(userPool)
        .cognitoDomain(CognitoDomainOptions.builder().domainPrefix(domainPrefix).build())
        .build()

    private val signInWithAppleSecret = signInWithAppleSecretArn?.let {
        Secret.fromSecretAttributes(this, "SignInWithAppleSecret", SecretAttributes.builder().secretCompleteArn(it).build())
    }

    /** The identity provider for SignInWithApple if [signInWithAppleSecret] are provided. */
    @Suppress("MemberVisibilityCanBePrivate")
    val signInWithAppleIdentityProvider: UserPoolIdentityProviderApple? =
        signInWithAppleSecret?.let { secret ->
            UserPoolIdentityProviderApple.Builder.create(this, "SignInWithAppleIdentityProvider")
                .userPool(userPool)
                .scopes(listOf("name", "email"))
                .teamId(secret.secretValueFromJson("team_id").unsafeUnwrap())
                .clientId(secret.secretValueFromJson("client_id").unsafeUnwrap())
                .keyId(secret.secretValueFromJson("key_id").unsafeUnwrap())
                .privateKey(secret.secretValueFromJson("private_key").unsafeUnwrap())
                .build()
        }

    /** The user pool client that can interact on the API level. */
    @Suppress("MemberVisibilityCanBePrivate")
    val userPoolClient: UserPoolClient = UserPoolClient.Builder.create(this, "UserPoolClient")
        .userPool(userPool)
        .supportedIdentityProviders(buildList {
            add(UserPoolClientIdentityProvider.COGNITO)
            if (signInWithAppleIdentityProvider != null) add(UserPoolClientIdentityProvider.APPLE)
        })
        .oAuth(
            OAuthSettings.builder()
                .callbackUrls(callbackUrls)
                .flows(OAuthFlows.builder().authorizationCodeGrant(true).build())
                .scopes(listOf(OAuthScope.OPENID, OAuthScope.EMAIL))
                .build()
        )
        .refreshTokenValidity(Duration.days(14))
        .accessTokenValidity(Duration.days(1))
        .idTokenValidity(Duration.days(1))
        .build()
        .export("UserPoolClientId", "ID of the user pool client") { it.userPoolClientId }

    init {
        signInWithAppleIdentityProvider?.also {
            // Fix for "The provider SignInWithApple does not exist for User Pool"
            userPoolClient.node.addDependency(it)
        }
    }
}
