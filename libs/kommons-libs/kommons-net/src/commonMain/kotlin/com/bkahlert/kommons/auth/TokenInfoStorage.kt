package com.bkahlert.kommons.auth

public interface TokenInfoStorage {
    public fun get(): TokenInfo?
    public fun set(tokenInfo: TokenInfo?)
}
