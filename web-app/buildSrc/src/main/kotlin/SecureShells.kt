import org.gradle.kotlin.dsl.delegateClosureOf
import java.io.File

fun org.hidetake.groovy.ssh.core.Service.runSessions(
    action: org.hidetake.groovy.ssh.core.RunHandler.() -> Unit,
) = run(delegateClosureOf(action))

fun org.hidetake.groovy.ssh.core.RunHandler.session(
    vararg remotes: org.hidetake.groovy.ssh.core.Remote,
    action: org.hidetake.groovy.ssh.session.SessionHandler.() -> Unit,
) = session(*remotes, delegateClosureOf(action))

fun org.hidetake.groovy.ssh.session.SessionHandler.put(
    from: Array<File>?,
    into: Any,
) = put(from?.toList() ?: emptyList(), into)

fun org.hidetake.groovy.ssh.session.SessionHandler.put(
    from: Iterable<File>,
    into: Any,
) = put(hashMapOf("from" to from, "into" to into))
