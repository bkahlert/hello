package com.bkahlert.kommons.auth

import com.bkahlert.kommons.json.LenientJson
import org.w3c.dom.Storage

/**
 * [OpenIDProviderMetadata] backed by the specified [storage].
 */
public class StorageOpenIDProviderMetadataCache(
    private val storage: Storage,
) : OpenIDProviderMetadataCache {
    private val OpenIDProvider.key get() = "<oidc,$url>"

    override fun get(provider: OpenIDProvider): OpenIDProviderMetadata? =
        kotlin.runCatching {
            val serialized = storage.getItem(provider.key)
            serialized?.let { LenientJson.decodeFromString(OpenIDProviderMetadata.serializer(), it) }
        }.getOrElse {
            console.warn("Failed to load OpenID provider metadata from cache", it)
            null
        }

    private fun set(provider: OpenIDProvider, metadata: OpenIDProviderMetadata) {
        kotlin.runCatching {
            val serialized = LenientJson.encodeToString(OpenIDProviderMetadata.serializer(), metadata)
            storage.setItem(provider.key, serialized)
        }.getOrElse {
            console.warn("Failed to save OpenID provider metadata in cache", it)
        }
    }

    override suspend fun getOrCompute(provider: OpenIDProvider, compute: suspend () -> OpenIDProviderMetadata): OpenIDProviderMetadata =
        get(provider) ?: compute().also { set(provider, it) }

    override fun evict(provider: OpenIDProvider) {
        storage.removeItem(provider.key)
    }

    override fun toString(): String =
        "StorageOpenIDProviderMetadataCache(${storage::class.simpleName})"
}
