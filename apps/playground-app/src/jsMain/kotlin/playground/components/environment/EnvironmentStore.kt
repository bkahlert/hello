@file:Suppress("RedundantVisibilityModifier")

package playground.components.environment

import com.bkahlert.hello.environment.data.EnvironmentDataSource
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.kommons.js.ConsoleLogging
import com.bkahlert.kommons.js.grouping
import dev.fritz2.core.Handler
import dev.fritz2.core.RootStore

public class EnvironmentStore(
    private val environmentDataSource: EnvironmentDataSource,
) : RootStore<Environment?>(null) {

    private val logger by ConsoleLogging

    val load: Handler<Unit> = handle { _, _ ->
        logger.grouping(EnvironmentDataSource::load) {
            environmentDataSource.load()
        }
    }

    init {
        load()
    }
}
