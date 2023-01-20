package com.bkahlert.hello.clickup.view

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures
import com.bkahlert.hello.clickup.view.Activity.TaskActivity
import com.bkahlert.semanticui.test.JQueryLibrary
import com.bkahlert.semanticui.test.SemanticUiLibrary
import com.bkahlert.semanticui.test.compositionWith
import com.bkahlert.semanticui.test.root
import io.kotest.matchers.shouldBe
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

class ActivityIconKtTest {

    @Test
    fun activity_icon() = runTest {
        compositionWith(JQueryLibrary, SemanticUiLibrary) {
            ActivityIcon(TaskActivity(ClickUpFixtures.task(id = "task-id")))
        }

        root { it.innerHTML shouldBe "<i class=\"square icon\" style=\"color: rgb(2, 188, 212);\"></i>" }
    }
}
