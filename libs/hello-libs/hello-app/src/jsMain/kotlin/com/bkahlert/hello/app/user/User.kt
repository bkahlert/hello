package com.bkahlert.hello.app.user

import com.bkahlert.hello.icon.heroicons.SolidHeroIcons
import com.bkahlert.kommons.auth.OpenIDStandardClaims
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import com.bkahlert.kommons.auth.UserInfo
import com.bkahlert.kommons.json.LenientJson
import com.bkahlert.kommons.ktor.JsonHttpClient
import com.bkahlert.kommons.uri.GravatarImageUri
import com.bkahlert.kommons.uri.Uri
import io.ktor.client.HttpClient
import kotlinx.serialization.json.decodeFromJsonElement

public data class User(
    public val session: AuthorizedSession,
) {
    public val claims: UserInfo get() = session.userInfo
    public val client: HttpClient by lazy { JsonHttpClient { session.installAuth(this) } }

    public val id: String get() = checkNotNull(claims.subjectIdentifier) { "Missing ${OpenIDStandardClaims::subjectIdentifier.name}" }
    public val nickname: String get() = claims.nickname ?: username ?: id
    public val username: String? get() = claims[USERNAME_CLAIM_NAME]?.let(LenientJson::decodeFromJsonElement)
    public val picture: Uri? get() = claims.picture
    public val email: String? get() = claims.email

    public companion object {
        /** Name for claim `username` */
        public const val USERNAME_CLAIM_NAME: String = "username"
    }
}

public val User.icon: Uri
    get() = picture ?: email?.let { GravatarImageUri(it, size = 256) } ?: SolidHeroIcons.user_circle
