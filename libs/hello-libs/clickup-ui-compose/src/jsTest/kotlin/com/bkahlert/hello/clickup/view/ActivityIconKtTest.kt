package com.bkahlert.hello.clickup.view

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.view.Activity.TaskActivity
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.testutils.TestScope
import com.bkahlert.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ActivityIconKtTest {

    @Test
    fun activity_icon() = runTest {
        composition {
            ActivityIcon(TaskActivity(ClickUpFixtures.task(id = "task-id")))
        }

        root.innerHTML shouldBe "<i class=\"square icon\" style=\"color: rgb(2, 188, 212);\"></i>"
    }
}
