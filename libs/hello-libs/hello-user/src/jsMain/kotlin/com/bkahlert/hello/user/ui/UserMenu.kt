package com.bkahlert.hello.user.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.bkahlert.hello.data.Resource
import com.bkahlert.hello.user.domain.User
import com.bkahlert.kommons.auth.diagnostics
import com.bkahlert.kommons.uri.DataUri
import com.bkahlert.semanticui.collection.Item
import com.bkahlert.semanticui.collection.LinkItem
import com.bkahlert.semanticui.collection.Menu
import com.bkahlert.semanticui.collection.MenuElement
import com.bkahlert.semanticui.core.attributes.Modifier.Variation.Size.Small
import com.bkahlert.semanticui.core.dom.SemanticAttrBuilderContext
import com.bkahlert.semanticui.core.dom.SemanticElementScope
import com.bkahlert.semanticui.custom.ErrorMessage
import com.bkahlert.semanticui.custom.LoadingState
import com.bkahlert.semanticui.custom.apply
import com.bkahlert.semanticui.element.Icon
import com.bkahlert.semanticui.element.Image
import com.bkahlert.semanticui.element.size
import com.bkahlert.semanticui.module.inverted
import io.ktor.http.ContentType
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Text

@Composable
public fun UserMenu(
    userResource: Resource<User?>?,
    onSignIn: (() -> Unit)? = null,
    onReauthorize: (() -> Unit)? = null,
    onForceReauthorize: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
    vararg items: @Composable SemanticElementScope<MenuElement>.() -> Unit,
    loadingState: LoadingState = if (userResource == null) LoadingState.On else LoadingState.Off,
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
) {
    when (userResource) {
        null -> RawUserMenu(
            loadingState = loadingState,
            attrs = attrs
        )

        is Resource.Success -> UserMenu(
            user = userResource.data,
            onSignIn = onSignIn,
            onReauthorize = onReauthorize,
            onForceReauthorize = onForceReauthorize,
            onSignOut = onSignOut,
            items = items,
            loadingState = loadingState,
            attrs = attrs,
        )

        is Resource.Failure -> RawUserMenu(
            { Item { ErrorMessage(userResource.message, userResource.cause) } },
            {
                if (onReauthorize != null) {
                    LinkItem({
                        onClick { onReauthorize() }
                    }) {
                        Icon("sync")
                        Text("Refresh")
                    }
                }
            },
            {
                if (onForceReauthorize != null) {
                    LinkItem({
                        onClick { onForceReauthorize() }
                    }) {
                        Icon("eraser")
                        Text("Force refresh")
                    }
                }
            },
            loadingState = loadingState,
            attrs = attrs,
        )
    }
}

@Composable
public fun UserMenu(
    user: User? = null,
    onSignIn: (() -> Unit)? = null,
    onReauthorize: (() -> Unit)? = null,
    onForceReauthorize: (() -> Unit)? = null,
    onSignOut: (() -> Unit)? = null,
    vararg items: @Composable SemanticElementScope<MenuElement>.() -> Unit,
    loadingState: LoadingState = LoadingState.Off,
    attrs: SemanticAttrBuilderContext<MenuElement>? = null,
) {
    RawUserMenu(
        { Item { Avatar(user) } },
        *items,
        {
            if (user == null && onSignIn != null) {
                LinkItem({
                    onClick { onSignIn() }
                }) {
                    Icon("sign-in")
                    Text("Sign-in")
                }
            }
        },
        {
            if (onReauthorize != null) {
                LinkItem({
                    onClick { onReauthorize() }
                }) {
                    Icon("sync")
//                    Text("Refresh")
                }
            }
        },
        {
            if (onForceReauthorize != null) {
                LinkItem({
                    onClick { onForceReauthorize() }
                }) {
                    Icon("eraser")
//                    Text("Force refresh")
                }
            }
        },
        {
            if (user != null && onSignOut != null) {
                LinkItem({
                    onClick { onSignOut() }
                }) {
                    Icon("sign-out")
                    Text("Sign-out")
                }
            }
        },
        loadingState = loadingState,
        attrs = attrs,
    )
}

@Composable
private fun RawUserMenu(
    vararg items: @Composable SemanticElementScope<MenuElement>.() -> Unit,
    loadingState: LoadingState,
    attrs: SemanticAttrBuilderContext<MenuElement>?,
) {
    Menu({
        apply(loadingState)
        attrs?.invoke(this)
    }) {
        apply(loadingState, dimmerAttrs = { v.inverted() }, loaderAttrs = { v.size(Small) })
        items.forEach { item ->
            item()
        }
    }
}

@Composable
public fun Avatar(
    user: User?,
) {
    A(attrs = { classes("ui", "avatar", "image") }) {
        if (user != null) {
            val nickname = remember(user) { user.nickname }
            val diagnostics = remember(user) { user.claims.diagnostics.toList().joinToString("\n") { (key, value) -> "$key: $value" } }
            Image(
                src = user.picture ?: avatarPlaceholderUri,
                alt = "Picture of $nickname",
            ) {
                style { cursor("help") }
                title("$nickname\n\nDiagnostics:\n$diagnostics")
            }
        } else {
            Image(
                src = AnonymousAvatarUri,
                alt = "Picture of anonymous",
            )
        }
    }
}

private val avatarPlaceholderUri: DataUri by lazy {
    DataUri(ContentType.Image.SVG, avatarPlaceholder.trim().encodeToByteArray())
}

//language=SVG
private const val avatarPlaceholder = """
<svg xmlns="http://www.w3.org/2000/svg" aria-label="Empty avatar" role="img" cursor="default" width="60" height="60">
    <circle r="26" cx="30" cy="30" fill="none" stroke="black" stroke-opacity="87%" stroke-width="4" stroke-dasharray="3,12" stroke-linecap="round"/>
    <g style="transform: translateY(10px);">
        <g transform="matrix(1,0,0,-1,197.42373,1300.6102)">
            <path fill="black" fill-opacity="87%" d="M-142.4,1252.8c0-2.8-0.9-5.1-2.6-6.7c-1.7-1.6-4-2.5-6.9-2.5h-31c-2.9,0-5.2,0.8-6.9,2.5c-1.7,1.6-2.6,3.9-2.6,6.7
          c0,1.3,0,2.5,0.1,3.7c0.1,1.2,0.2,2.5,0.5,3.9c0.2,1.4,0.6,2.7,0.9,3.9s0.9,2.3,1.5,3.5s1.4,2.1,2.2,2.9s1.8,1.4,3,1.9
          c1.2,0.5,2.5,0.7,4,0.7c0.2,0,0.7-0.3,1.5-0.8s1.7-1.1,2.6-1.7c1-0.6,2.3-1.2,3.8-1.7c1.6-0.5,3.2-0.8,4.7-0.8s3.2,0.3,4.7,0.8
          c1.6,0.5,2.9,1.1,3.8,1.7c1,0.6,1.9,1.2,2.6,1.7s1.3,0.8,1.5,0.8c1.4,0,2.8-0.2,4-0.7c1.2-0.5,2.2-1.1,3-1.9s1.6-1.8,2.2-2.9
          s1.1-2.3,1.5-3.5s0.7-2.5,0.9-3.9c0.2-1.4,0.4-2.7,0.5-3.9C-142.5,1255.3-142.4,1254.1-142.4,1252.8z M-153.8,1284.2
          c0-3.8-1.3-7-4-9.6c-2.7-2.7-5.9-4-9.6-4s-7,1.3-9.6,4c-2.7,2.7-4,5.9-4,9.6s1.3,7,4,9.6c2.7,2.7,5.9,4,9.6,4s7-1.3,9.6-4
          C-155.1,1291.2-153.8,1288-153.8,1284.2z"/>
        </g>
    </g>
</svg>
"""

private val AnonymousAvatarUri: DataUri by lazy {
    DataUri(ContentType.Image.SVG, AnonymousAvatarSvg.trim().encodeToByteArray())
}

//language=SVG
private const val AnonymousAvatarSvg = """
<svg xmlns="http://www.w3.org/2000/svg" aria-label="Empty avatar" role="img" cursor="default" width="60" height="60">
    <circle r="26" cx="30" cy="30" fill="none" stroke="black" stroke-opacity="87%" stroke-width="4" stroke-dasharray="3,12" stroke-linecap="round"/>
</svg>
"""
