package com.bkahlert.kommons.auth

import com.bkahlert.kommons.json.LenientJson
import io.ktor.util.encodeBase64
import org.w3c.dom.Storage

/**
 * [TokenInfoStorage] backed by the specified [storage].
 */
public class StorageTokenInfoStorage(
    private val storage: Storage,
) : TokenInfoStorage {
    private fun OpenIDProvider.key(clientId: String) = "<t:${clientId.encodeBase64()},$url>"

    override fun get(provider: OpenIDProvider, clientId: String): TokenInfo? =
        kotlin.runCatching {
            val serialized = storage.getItem(provider.key(clientId))
            serialized?.let { LenientJson.decodeFromString(TokenInfo.serializer(), it) }
        }.getOrElse {
            console.warn("Failed to load token info from storage", it)
            null
        }

    override fun set(provider: OpenIDProvider, clientId: String, tokenInfo: TokenInfo) {
        kotlin.runCatching {
            val serialized = LenientJson.encodeToString(TokenInfo.serializer(), tokenInfo)
            storage.setItem(provider.key(clientId), serialized)
        }.getOrElse {
            console.warn("Failed to save token info in storage", it)
        }
    }

    public override fun remove(provider: OpenIDProvider, clientId: String) {
        storage.removeItem(provider.key(clientId))
    }

    override fun toString(): String =
        "StorageTokenInfoStorage(${storage::class.simpleName})"
}
