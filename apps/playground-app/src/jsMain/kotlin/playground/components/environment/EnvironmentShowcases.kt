@file:Suppress("RedundantVisibilityModifier")

package playground.components.environment

import com.bkahlert.hello.environment.data.DynamicEnvironmentDataSource
import com.bkahlert.hello.environment.domain.Environment
import playground.components.Page
import playground.components.showcase.showcase
import playground.components.showcase.showcases
import playground.tailwind.heroicons.HeroIcons

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
