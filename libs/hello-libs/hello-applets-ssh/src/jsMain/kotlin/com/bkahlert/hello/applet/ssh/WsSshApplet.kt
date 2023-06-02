package com.bkahlert.hello.applet.ssh

import com.bkahlert.hello.applet.Applet
import com.bkahlert.hello.applet.AppletEditor
import com.bkahlert.hello.applet.AspectRatio
import com.bkahlert.hello.applet.panel
import com.bkahlert.hello.font.FontFamilies
import com.bkahlert.hello.editor.IntLens
import com.bkahlert.hello.editor.UriLens
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.hello.socketio.client.Socket
import com.bkahlert.hello.socketio.client.io
import com.bkahlert.hello.socketio.client.onConnect
import com.bkahlert.hello.socketio.client.onDisconnect
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import js.core.jso
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

@Serializable
public data class WsSshApplet(
    override val id: String,
    @SerialName("title") @JsonNames("name") override val title: String? = null,
    @SerialName("server") val server: Uri? = null,
    @SerialName("host") val host: String? = null,
    @SerialName("port") val port: Int? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("password") val password: String? = null,
) : Applet {
    override fun editor(isNew: Boolean): AppletEditor<*> = WsSshAppletEditor(isNew, this)

    override fun render(renderContext: Tag<Element>): HtmlTag<HTMLDivElement> = renderContext.panel(AspectRatio.stretch) {
        val missing = listOf(::server, ::host).filter { it.get().isNullOrBlank() }
        if (missing.isNotEmpty()) {
            renderConfigurationMissing(missing)
        } else {
            val socket: Socket = io(server.toString(), jso {
                path = "/ssh/socket.io"
                // transports: ['websocket', 'polling'],
            }).apply {
                onConnect() handledBy {
                    emit(
                        "auth", json(
                            "host" to host,
                            "port" to port,
                            "username" to username,
                            "password" to password,
                        )
                    )
                }
                onDisconnect() handledBy {
                    io.reconnection(false)
                }
            }

            SocketTerminal(socket) {
                cursorBlink = true
                fontFamily = FontFamilies.MONOSPACE
            }.render(this)
        }
    }

    public companion object {
        public fun title(): Lens<WsSshApplet, String> =
            WsSshApplet::title.lens({ it.title }) { p, v -> p.copy(title = v) }.orEmpty()

        public fun server(): Lens<WsSshApplet, String> =
            WsSshApplet::server.lens({ it.server }, { p, v -> p.copy(server = v?.toUriOrNull()) }) + UriLens

        public fun host(): Lens<WsSshApplet, String> =
            WsSshApplet::host.lens({ it.host }, { p, v -> p.copy(host = v) }).orEmpty()

        public fun port(): Lens<WsSshApplet, String> =
            WsSshApplet::port.lens({ it.port }, { p, v -> p.copy(port = v) }) + IntLens

        public fun username(): Lens<WsSshApplet, String> =
            WsSshApplet::username.lens({ it.username }, { p, v -> p.copy(username = v) }).orEmpty()

        public fun password(): Lens<WsSshApplet, String> =
            WsSshApplet::password.lens({ it.password }, { p, v -> p.copy(password = v) }).orEmpty()
    }
}
