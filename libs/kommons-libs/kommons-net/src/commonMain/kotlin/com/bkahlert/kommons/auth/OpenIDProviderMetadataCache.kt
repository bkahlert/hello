package com.bkahlert.kommons.auth

/**
 * Cache for [OpenIDProviderMetadata]
 */
public interface OpenIDProviderMetadataCache {
    public fun get(provider: OpenIDProvider): OpenIDProviderMetadata?
    public suspend fun getOrCompute(provider: OpenIDProvider, compute: suspend () -> OpenIDProviderMetadata): OpenIDProviderMetadata
    public fun evict(provider: OpenIDProvider)
}
