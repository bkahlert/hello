package com.bkahlert.kommons.oauth

import com.bkahlert.kommons.oauth.OAuth2ResourceServer.Scope
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.pathSegments
import com.bkahlert.kommons.uri.port
import com.bkahlert.kommons.uri.toUri
import io.ktor.http.Url

public interface OAuth2ResourceServer {
    /** Human-readable name of the resource server. */
    public val name: String

    /** Identifier of the resource server, for example, a domain name or [Uri]. */
    public val identifier: String

    /** Scopes used by the resource server. */
    public val scopes: List<Scope>

    /** Returns whether the given [Uri] belongs to the resource. */
    public fun matches(uri: Uri): Boolean

    public interface Scope {
        public val name: String
        public val description: String
    }
}

/**
 * An [OAuth2ResourceServer] providing an API
 * with the specified [name] located at the specified [endpoint].
 *
 * The [identifier] default to the [Uri.host] if available, and
 * [Uri.toString] otherwise.
 */
public class API(
    override val name: String,
    override val identifier: String,
    private val endpoint: Uri,
    vararg scopes: Pair<String, String>,
) : OAuth2ResourceServer {
    public constructor(
        name: String,
        endpoint: Uri,
        vararg scopes: Pair<String, String>,
    ) : this(name, endpoint.host ?: endpoint.toString(), endpoint, *scopes)

    override val scopes: List<Scope> = scopes.map { (name, description) ->
        object : Scope {
            override val name: String get() = name
            override val description: String get() = description
        }
    }

    override fun matches(uri: Uri): Boolean =
        uri.scheme == endpoint.scheme &&
            uri.host == endpoint.host &&
            uri.port == endpoint.port &&
            uri.pathSegments.zip(endpoint.pathSegments).all {
                it.first == it.second
            }
}

/** Returns whether the given [Url] belongs to the resource. */
@Deprecated("use URI")
public fun OAuth2ResourceServer.matches(url: Url): Boolean = matches(url.toUri())
