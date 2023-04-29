package com.bkahlert.hello.components.applet.ssh

import com.bkahlert.hello.components.applet.Applet
import com.bkahlert.hello.components.applet.AppletEditor
import com.bkahlert.hello.components.applet.AspectRatio
import com.bkahlert.hello.components.applet.panel
import com.bkahlert.hello.fritz2.IntLens
import com.bkahlert.hello.fritz2.UriLens
import com.bkahlert.hello.fritz2.components.FontFamilies
import com.bkahlert.hello.fritz2.lens
import com.bkahlert.hello.fritz2.mergeValidationMessages
import com.bkahlert.hello.fritz2.orEmpty
import com.bkahlert.kommons.uri.Uri
import com.bkahlert.kommons.uri.toUriOrNull
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Lens
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.max
import dev.fritz2.core.min
import dev.fritz2.core.placeholder
import dev.fritz2.core.type
import dev.fritz2.core.values
import dev.fritz2.headless.components.inputField
import js.core.jso
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import kotlin.js.json

@Serializable
data class WsSshApplet(
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

    companion object {
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

class WsSshAppletEditor(isNew: Boolean, applet: WsSshApplet) : AppletEditor<WsSshApplet>(isNew, applet) {
    override fun RenderContext.renderFields() {
        inputField {
            val store = map(WsSshApplet.title())
            value(store)
            inputLabel {
                +"Title"
                inputTextfield {
                    type("text")
                    placeholder("Workstation")
                    keyups.values() handledBy store.update
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.server()))
            inputLabel {
                +"Server"
                inputTextfield {
                    type("url")
                    placeholder("ssh.proxy.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.host()))
            inputLabel {
                +"Host"
                inputTextfield {
                    type("text")
                    placeholder("ssh.example.com")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.port()))
            inputLabel {
                +"Port"
                inputTextfield {
                    type("number")
                    min("1")
                    max("65535")
                    placeholder("22")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.username()))
            inputLabel {
                +"Username"
                inputTextfield {
                    type("text")
                }.also(::mergeValidationMessages)
            }
        }
        inputField {
            value(map(WsSshApplet.password()))
            inputLabel {
                +"Password"
                inputTextfield {
                    type("password")
                }.also(::mergeValidationMessages)
            }
        }
    }
}


// TODO use fritz2 socket
// TODO connect when into view


fun SocketTerminal.render(
    renderContext: RenderContext,
): HtmlTag<HTMLDivElement> = renderContext.div("flex flex-col overflow-hidden") {
    div("flex-1 overflow-hidden [&>.xterm]:w-full [&>.xterm]:h-full") { attach(this) }
    div("terminal__footer flex items-center gap-1 bg-slate-800 font-bold text-sm p-1") {
        div("terminal__connection flex-1") {
            titleUpdates.render(this) { title ->
                className("text-white/60")
                +title
            }
        }
        div("terminal__status flex-1") {
            statusUpdates.render(this) { status ->
                when (status) {
                    is SocketTerminal.StatusUpdate.Disconnected -> {
                        className("text-emerald-500/60")
                        +"Disconnected: ${status.status}"
                    }

                    is SocketTerminal.StatusUpdate.Disconnecting -> {
                        className("text-amber-500")
                        +"Disconnecting in ${status.remaining}"
                    }

                    is SocketTerminal.StatusUpdate.Error -> {
                        className("text-red-500")
                        +"Error: ${status.status}"
                    }

                    is SocketTerminal.StatusUpdate.Info -> {
                        className("text-emerald-500")
                        +status.status
                    }

                    is SocketTerminal.StatusUpdate.SshError -> {
                        className("text-fuchsia-500")
                        +"SSH Error: ${status.status}"
                    }
                }
            }
        }
    }
}
