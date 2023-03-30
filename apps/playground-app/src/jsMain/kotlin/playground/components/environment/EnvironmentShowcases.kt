@file:Suppress("RedundantVisibilityModifier")

package playground.components.environment

import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.domain.Environment
import com.bkahlert.hello.fritz2.components.Page
import com.bkahlert.hello.fritz2.components.heroicons.HeroIcons
import com.bkahlert.hello.fritz2.components.showcase.showcase
import com.bkahlert.hello.fritz2.components.showcase.showcases

object EnvironmentShowcases : Page(
    "environment",
    "Environment",
    "Environment showcases",
    heroIcon = HeroIcons::command_line,
    content = {
        showcases("EnvironmentView") {
            showcase("Loading") {
                environmentView(null)
            }
            showcase("Empty") {
                environmentView(Environment.EMPTY)
            }
            showcase("Filled") {
                environmentView(Environment("FOO" to "bar", "BAZ" to ""))
            }
            showcase("Actual") {
                val repository = EnvironmentStore(DynamicEnvironmentDataSource())
                repository.data.render { environmentView(it) }
            }
        }
    },
)
