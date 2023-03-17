@file:Suppress("RedundantVisibilityModifier")

package playground.components.user

import com.bkahlert.hello.user.domain.User
import com.bkahlert.kommons.auth.Session.AuthorizedSession
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import playground.components.session.SessionStore

public class UserStore(
    private val sessionStore: SessionStore,
) : Store<User?> by sessionStore.map(
    lensOf(
        id = "user",
        getter = { (it as? AuthorizedSession)?.let(::User) },
        setter = { session, _ -> session },
    )
) {

    public val signIn = sessionStore.authorize
    public val reauthorize = sessionStore.reauthorize
    public val signOut = sessionStore.unauthorize
}
