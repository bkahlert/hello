package com.bkahlert.hello.api.auth

import aws.smithy.kotlin.runtime.util.decodeBase64
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.bkahlert.hello.aws.lambda.EventHandler
import com.bkahlert.hello.aws.lambda.json
import com.bkahlert.hello.aws.lambda.withMimeType
import com.bkahlert.kommons.logging.SLF4J

class AuthHandler : EventHandler() {

    private val logger by SLF4J
    private val authInfo by lazy {
        mapOf(
            "cognito-domain" to System.getenv("COGNITO_DOMAIN"),
            "cognito-client-id" to System.getenv("COGNITO_WEB_APP_CLIENT_ID"),
        )
    }

    override suspend fun handleEvent(
        event: APIGatewayV2HTTPEvent,
        context: Context,
    ): APIGatewayV2HTTPResponse {
        logger.dump(event, context)
        return when (event.routeKey) {
            "GET /user" -> {
                /*
                const jwt = require("jsonwebtoken");
                const jwkToPem = require("jwk-to-pem");
                const fetch = require("node-fetch");
                const util = require("util");

                const getOpenIdConfig = (() => {
                    let prom = undefined;
                    return () => prom = (prom || (async () => {
                        const openIdRes = await fetch(`https://cognito-idp.${process.env.AWS_REGION}.amazonaws.com/${process.env.USER_POOL_ID}/.well-known/openid-configuration`);
                        if (!openIdRes.ok) {
                            throw new Error(openIdRes);
                        }
                        const openIdJson = await openIdRes.json();
                        const res = await fetch(openIdJson.jwks_uri);
                        if (!res.ok) {
                            throw new Error(res);
                        }
                        const jwks = await res.json();
                        return {
                            openIdJson,
                            jwks,
                        };
                    })());
                })();

                module.exports.handler = async (event) => {
                    const auth_token = event.headers.authorization.split("Bearer ")[1];
                    const openIdConfig = await getOpenIdConfig();
                    const decoded = jwt.decode(auth_token, {complete: true});
                    const jwk = openIdConfig.jwks.keys.find(({kid}) => kid === decoded.header.kid);
                    const pem = jwkToPem(jwk);
                    const token_use = decoded.payload.token_use;
                    if (token_use === "access") {
                        await util.promisify(jwt.verify.bind(jwt))(auth_token, pem, { algorithms: ["RS256"], issuer: openIdConfig.openIdJson.issuer});

                        if (decoded.payload.client_id !== process.env.CLIENT_ID) {
                            throw new Error(`ClientId must be ${process.env.CLIENT_ID}, got ${decoded.payload.client_id}`);
                        }

                        const openIdRes = await fetch(openIdConfig.openIdJson.userinfo_endpoint, {
                            headers: new fetch.Headers({"Authorization": `Bearer ${auth_token}`}),
                        });
                        if (!openIdRes.ok) {
                            throw new Error(JSON.stringify(await openIdRes.json()));
                        }
                    }else if (token_use === "id") {
                        await util.promisify(jwt.verify.bind(jwt))(auth_token, pem, { algorithms: openIdConfig.openIdJson.id_token_signing_alg_values_supported, issuer: openIdConfig.openIdJson.issuer, audience: process.env.CLIENT_ID});
                    }else {
                        throw new Error(`token_use must be "access" or "id", got ${token_use}`);
                    }
                    const userId = decoded.payload.sub;

                    return {userId};
                };

                 */
                val payload = event.headers["authorization"]?.let {
                    val (scheme, token) = it.split(' ', limit = 2)
                    require(scheme == "Bearer") { "unexpected scheme $scheme" }
                    val (header, payload, signature) = token.split('.')
                    payload.decodeBase64()
                }
                when (payload) {
                    null -> throw IllegalArgumentException("Missing token")

                    else -> APIGatewayV2HTTPResponse.builder()
                        .withStatusCode(200)
                        .withMimeType { APPLICATION_JSON }
                        .withBody(payload)
                        .build()
                }
            }

            else -> APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withMimeType { APPLICATION_JSON }
                .withBody(json(authInfo))
                .build()
        }
    }
}
