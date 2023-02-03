package com.bkahlert.kommons.auth

/**
 * Storage for [TokenInfo]
 */
public interface TokenInfoStorage {
    public fun get(provider: OpenIDProvider, clientId: String): TokenInfo?
    public fun set(provider: OpenIDProvider, clientId: String, tokenInfo: TokenInfo)
    public fun remove(provider: OpenIDProvider, clientId: String)
}
