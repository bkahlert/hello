package com.bkahlert.hello.widget.ssh

import com.bkahlert.hello.fritz2.observedResizes
import com.bkahlert.hello.socketio.client.Socket
import com.bkahlert.hello.socketio.client.on
import com.bkahlert.hello.socketio.client.onConnect
import com.bkahlert.hello.xterm.FitAddon
import com.bkahlert.hello.xterm.ITerminalOptions
import com.bkahlert.hello.xterm.Terminal
import com.bkahlert.hello.xterm.XTermCss
import com.bkahlert.hello.xterm.data
import com.bkahlert.hello.xterm.dimensions
import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Tag
import dev.fritz2.core.handledBy
import js.core.jso
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public class SocketTerminal(
    public val socket: Socket,
    options: ITerminalOptions.() -> Unit,
) {
    private val terminal = Terminal(jso<ITerminalOptions>(options))
    private val terminalFitAddon = FitAddon().also { terminal.loadAddon(it) }

    private fun updateSize() {
        terminalFitAddon.fit()
        if (socket.connected) socket.emit("resize", terminal.dimensions)
    }

    init {
        XTermCss
        terminal.data handledBy { socket.emit("data", it) }
        socket.on<String>("data") handledBy { terminal.write(it) }
        socket.onConnect() handledBy {
            while (terminalFitAddon.proposeDimensions() == undefined) delay(100)
            updateSize()
        }
    }

    /** Renders the terminal inside the specified [tag]. */
    public fun attach(tag: Tag<HTMLElement>) {
        terminal.open(tag.domNode)
        tag.observedResizes handledBy { updateSize() }
    }

    /** A flow of title updates. */
    public val titleUpdates: Flow<String>
        get() = socket.on("title")

    /** A flow of status updates. */
    public val statusUpdates: Flow<StatusUpdate>
        get() = merge(
            socket.on<String>("status").map { StatusUpdate.Info(it) },
            socket.on<Error>("error").map { StatusUpdate.Error(it.toString()) },
            socket.on<String>("ssherror").map { StatusUpdate.SshError(it) },
            socket.on<Int>("shutdownCountdownUpdate")
                .map { StatusUpdate.Disconnecting("Server is shutting down", it.seconds) },
            socket.on<String>("disconnect").map { StatusUpdate.Disconnected(it) }
        )

    public sealed interface StatusUpdate {
        public val status: String

        public class Info(override val status: String) : StatusUpdate
        public class Error(override val status: String) : StatusUpdate
        public class SshError(override val status: String) : StatusUpdate
        public class Disconnecting(override val status: String, public val remaining: Duration) : StatusUpdate
        public class Disconnected(override val status: String) : StatusUpdate
    }
}

// TODO use fritz2 socket
// TODO connect when into view


public fun SocketTerminal.render(
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
