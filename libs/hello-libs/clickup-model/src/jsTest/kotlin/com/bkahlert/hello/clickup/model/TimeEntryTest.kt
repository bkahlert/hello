package com.bkahlert.hello.clickup.model

import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.Teams
import com.bkahlert.hello.clickup.model.fixtures.ClickUpFixtures.UserJson
import com.bkahlert.hello.clickup.serialization.BasicSerializerTest
import com.bkahlert.hello.clickup.serialization.Named
import com.bkahlert.hello.clickup.serialization.NamedSerializer

@Suppress("unused")
class TimeEntryTest : BasicSerializerTest<Named<TimeEntry>>(
    NamedSerializer(TimeEntry.serializer()),
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
          "space_id": "1"
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
        "wid": "${Teams.first().id.stringValue}",
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
    """.trimIndent()
) {

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
}
