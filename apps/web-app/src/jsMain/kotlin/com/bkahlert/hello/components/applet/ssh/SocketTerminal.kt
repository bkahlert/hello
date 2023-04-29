package com.bkahlert.hello.components.applet.ssh

import com.bkahlert.hello.fritz2.observedResizes
import dev.fritz2.core.Tag
import dev.fritz2.core.handledBy
import js.core.jso
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.w3c.dom.HTMLElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SocketTerminal(
    val socket: Socket,
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
    fun attach(tag: Tag<HTMLElement>) {
        terminal.open(tag.domNode)
        tag.observedResizes handledBy { updateSize() }
    }

    /** A flow of title updates. */
    val titleUpdates: Flow<String>
        get() = socket.on("title")

    /** A flow of status updates. */
    val statusUpdates: Flow<StatusUpdate>
        get() = merge(
            socket.on<String>("status").map { StatusUpdate.Info(it) },
            socket.on<Error>("error").map { StatusUpdate.Error(it.toString()) },
            socket.on<String>("ssherror").map { StatusUpdate.SshError(it) },
            socket.on<Int>("shutdownCountdownUpdate")
                .map { StatusUpdate.Disconnecting("Server is shutting down", it.seconds) },
            socket.on<String>("disconnect").map { StatusUpdate.Disconnected(it) }
        )

    sealed interface StatusUpdate {
        val status: String

        class Info(override val status: String) : StatusUpdate
        class Error(override val status: String) : StatusUpdate
        class SshError(override val status: String) : StatusUpdate
        class Disconnecting(override val status: String, val remaining: Duration) : StatusUpdate
        class Disconnected(override val status: String) : StatusUpdate
    }
}
