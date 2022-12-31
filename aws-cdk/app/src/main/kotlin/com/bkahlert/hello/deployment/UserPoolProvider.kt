package com.bkahlert.hello.deployment

import software.amazon.awscdk.RemovalPolicy
import software.amazon.awscdk.services.cognito.CognitoDomainOptions
import software.amazon.awscdk.services.cognito.OAuthFlows
import software.amazon.awscdk.services.cognito.OAuthScope
import software.amazon.awscdk.services.cognito.OAuthSettings
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.cognito.UserPoolClient
import software.amazon.awscdk.services.cognito.UserPoolClientIdentityProvider
import software.amazon.awscdk.services.cognito.UserPoolDomain
import software.amazon.awscdk.services.cognito.UserPoolIdentityProviderApple
import software.amazon.awscdk.services.secretsmanager.ISecret
import software.constructs.Construct

/**
 * Defines a Cognito-backed OpenID provider.
 */
class UserPoolProvider(
    /** The scope in which to define this construct. */
    scope: Construct,
    /** The scoped construct ID. */
    id: String,
    /** The name of the corresponding [userPool]. */
    val name: String,
    /** The domain prefix of the corresponding [userPoolDomain]. */
    val domainPrefix: String,
    /** The callback URL of the corresponding [userPoolClient]. */
    val callbackUrl: String,
    /** The credentials of the corresponding [signInWithAppleIdentityProvider]. */
    val signInWithAppleSecret: ISecret? = null,
) : Construct(scope, id) {

    /** The user pool for storing user information. */
    @Suppress("MemberVisibilityCanBePrivate")
    val userPool: UserPool = UserPool.Builder.create(this, "UserPool")
        .userPoolName(name)
//        .selfSignUpEnabled(true)
//        .signInAliases(SignInAliases.builder().email(true).username(true).build())
        .removalPolicy(RemovalPolicy.DESTROY)
        .build()

    /** The user pool domain hosting features like the sign-up feature. */
    @Suppress("MemberVisibilityCanBePrivate")
    val userPoolDomain: UserPoolDomain = UserPoolDomain.Builder.create(this, "UserPoolDomain")
        .userPool(userPool)
        .cognitoDomain(CognitoDomainOptions.builder().domainPrefix(domainPrefix).build())
        .build()

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
                .callbackUrls(listOf(callbackUrl))
                .flows(OAuthFlows.builder().authorizationCodeGrant(true).build())
                .scopes(listOf(OAuthScope.OPENID, OAuthScope.EMAIL))
                .build()
        )
        .build()

    init {
        signInWithAppleIdentityProvider?.also {
            // Fix for "The provider SignInWithApple does not exist for User Pool"
            userPoolClient.node.addDependency(it)
        }
    }
}
