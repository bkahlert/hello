package com.bkahlert.hello.applets

import com.bkahlert.hello.fritz2.components.heroicons.SolidHeroIcons
import com.bkahlert.hello.fritz2.inputEditor
import com.bkahlert.hello.fritz2.mapValidating
import com.bkahlert.hello.fritz2.passwordEditor
import com.bkahlert.hello.fritz2.uriEditor
import com.bkahlert.kommons.dom.uri
import com.bkahlert.kommons.randomString
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.div
import com.bkahlert.kommons.uri.host
import com.bkahlert.kommons.uri.toUri
import com.bkahlert.kommons.uri.toUrl
import dev.fritz2.core.RenderContext
import dev.fritz2.core.action
import dev.fritz2.core.lensOf
import dev.fritz2.core.method
import dev.fritz2.core.name
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import dev.fritz2.core.target
import dev.fritz2.core.type
import dev.fritz2.core.value
import dev.fritz2.validation.ValidationMessage
import io.ktor.http.URLBuilder
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("webssh2")
data class WebSsh2Applet(
    override val id: String = randomString(),
    override val name: String,
    @SerialName("proxy") val proxy: Uri,
    @SerialName("host") val host: String,
    @SerialName("port") val port: Int? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("userpassword") val userpassword: String? = null,
) : Applet {

    override val icon: Uri get() = WebSsh2Applet.icon

    override fun duplicate(): Applet = copy(id = randomString())

    val uri: Uri get() = proxy / "host" / host
    val uriWithPort: Uri
        get() = if (port == null) uri else URLBuilder(uri.toUrl()).apply { parameters["port"] = this@WebSsh2Applet.port.toString() }.build().toUri()

    val postVariables = listOf(
        "username" to username,
        "userpassword" to userpassword,
    ).mapNotNull { (k, v) -> v?.let { k to it } }

    val windowName get() = "webssh2-$id"

    override fun render(renderContext: RenderContext) {
        renderContext.window(name, AspectRatio.fill) {
            if (postVariables.isEmpty()) {
                iframe("w-full h-full border-0") {
                    src(uriWithPort.toString())
                    allow(PermissionPolicy.values().asList())
                    sandbox(ContentSecurityPolicy.values().asList())
                }
            } else {
                form {
                    action(uriWithPort.toString())
                    method("post")
                    target(windowName)
                    postVariables.forEach { (name, value) ->
                        input {
                            type("hidden")
                            name(name)
                            value(value)
                        }
                    }
                    button("btn") {
                        type("submit")
                        +"Connect"
                    }
                }
                iframe("w-full h-full border-0") {
                    name(windowName)
                    src(uriWithPort.toString())
                    allow(PermissionPolicy.values().asList())
                    sandbox(ContentSecurityPolicy.values().asList())
                }
            }
        }
    }

    override fun renderEditor(renderContext: RenderContext, contributeMessages: (Flow<List<ValidationMessage>>) -> Unit): Flow<Applet> {
        val store = storeOf(this)
        renderContext.div("flex flex-col sm:flex-row gap-8 justify-center") {
            div("flex-grow flex flex-col gap-2") {
                label {
                    +"Name"
                    inputEditor(null, store.mapValidating(lensOf("name", { it.name }, { p, v ->
                        require(v.isNotBlank()) { "Name must not be blank" }
                        p.copy(name = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Proxy"
                    uriEditor(null, store.map(lensOf("proxy", { it.proxy }, { p, v -> p.copy(proxy = v) })))
                }
                label {
                    +"Host"
                    inputEditor(null, store.mapValidating(lensOf("host", { it.host }, { p, v ->
                        require(v.isNotBlank()) { "Host must not be blank" }
                        p.copy(host = v)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Port"
                    inputEditor(null, store.mapValidating(lensOf("port", { it.port?.toString() ?: "" }, { p, v ->
                        val portRange = 1..65535
                        val parsed = v.takeUnless { it.isBlank() }?.toIntOrNull()
                        require(v.isBlank() || parsed?.let { it in portRange } == true) { "Port must be blank or in $portRange" }
                        p.copy(port = parsed)
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Username"
                    inputEditor(null, store.mapValidating(lensOf("username", { it.username ?: "" }, { p, v ->
                        p.copy(username = v.takeUnless { it.isBlank() })
                    })).also { contributeMessages(it.messages) })
                }
                label {
                    +"Password"
                    passwordEditor(null, store.mapValidating(lensOf("userpassword", { it.userpassword ?: "" }, { p, v ->
                        p.copy(userpassword = v.takeUnless { it.isBlank() })
                    })).also { contributeMessages(it.messages) })
                }
            }
        }
        return store.data
    }

    companion object : AppletType<WebSsh2Applet> {
        override val name: String = "WebSSH2"
        override val description: String = "Connect to a SSH server via a WebSSH2 proxy"
        override val icon: Uri = SolidHeroIcons.command_line
        override val default = WebSsh2Applet(
            name = "WebSSH2",
            proxy = window.location.uri,
            host = window.location.uri.host ?: "localhost",
        )
    }
}
