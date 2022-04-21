package com.bkahlert.hello.clickup

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.plugins.clickup.PomodoroTimer
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.Teams
import com.bkahlert.hello.ui.demo.clickup.ClickUpFixtures.UserJson
import com.bkahlert.kommons.serialization.BasicSerializerTest
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.NamedSerializer
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import io.kotest.matchers.string.shouldContain
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

@Suppress("unused")
class TimeEntryTest : BasicSerializerTest<Named<TimeEntry>>(NamedSerializer(TimeEntry.serializer()),
    // language=JSON
    """
    {
      "data": {
        "id": "3873003127832353210",
        "task": {
          "id": "30jg1er",
          "name": "get things done",
          "status": {
            "status": "in progress",
            "color": "#a875ff",
            "type": "custom",
            "orderindex": 1
          },
          "custom_type": null
        },
        "wid": "3576831",
        "user": $UserJson,
        "billable": false,
        "start": "1647040470454",
        "duration": -13523,
        "description": "",
        "tags": [],
        "source": "clickup",
        "at": "1647040470454",
        "task_location": {
          "list_id": "25510969",
          "folder_id": "11087491",
          "space_id": "4564985"
        },
        "task_url": "https://app.clickup.com/t/20jg1er"
      }
    }
    """.trimIndent(),
    // language=JSON
    """
    {
      "data": {
        "id": "2874960270525506934",
        "wid": "${this.Teams.first<Team>().id.stringValue}",
        "user": $UserJson,
        "billable": false,
        "start": "1647157125275",
        "duration": -117108,
        "description": "",
        "tags": [],
        "source": "clickup",
        "at": "1647157125275",
        "task_location": {
          "list_id": null,
          "folder_id": null,
          "space_id": null
        },
        "task_url": "https://app.clickup.com/t/null"
      }
    }
    """.trimIndent()) {

    val request = """
        {
            "tid": "20jg1er",
            "description": "hello again",
            "billable": true,
            "tags": [
                {
                    "name": "tag1",
                    "tag_fg": "hsl(329deg, 73.2%, 43.9%)",
                    "tag_bg": "hsl(198deg, 76.7%, 51.2%)"
                },
                {
                    "name": "tag2"
                }
            ]
        }
    """.trimIndent()

    // https://github.com/JetBrains/compose-jb/tree/master/tutorials/Web/Using_Test_Utils
    @Test
    fun testDateFormat() = runTest {
        composition {
            PomodoroTimer(jsons.first().deserialize<Named<TimeEntry>>().value)
        }

        console.log(root.innerHTML)

        root.innerHTML shouldContain "12.03.2022"
    }
}
