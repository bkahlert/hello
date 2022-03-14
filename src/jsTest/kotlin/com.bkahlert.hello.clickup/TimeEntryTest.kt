package com.bkahlert.hello.clickup

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.integration.ActiveTask
import com.bkahlert.hello.test.JOHN
import com.bkahlert.hello.test.response
import com.bkahlert.kommons.serialization.BasicSerializerTest
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.NamedSerializer
import io.kotest.matchers.string.shouldContain
import org.jetbrains.compose.web.testutils.runTest
import kotlin.test.Test

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
        "user": {
          "id": 3687596,
          "username": "John Doe",
          "email": "john.doe@example.com",
          "color": "#4169E1",
          "initials": "JD",
          "profilePicture": "$JOHN"
        },
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
        "wid": "2576831",
        "user": {
          "id": 4687596,
          "username": "Björn Kahlert",
          "email": "mail@bkahlert.com",
          "color": "#4169E1",
          "initials": "BK",
          "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
        },
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

    val x = """
    {"data":{"id":"2874288493751214437","task":{"id":"20jg1er","name":"hello.bkahlert.com","status":{"status":"in progress","color":"#a875ff","type":"custom","orderindex":1}},"wid":"2576831","user":{"id":4687596,"username":"Björn Kahlert","email":"mail@bkahlert.com","color":"#4169E1","initials":"BK","profilePicture":"https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"},"billable":true,"start":1647117084159,"duration":-279,"description":"hello again","tags":[{"name":"tag1","tag_fg":"hsl(329deg, 73.2%, 43.9%)","tag_bg":"hsl(198deg, 76.7%, 51.2%)"},{"name":"tag2"}],"at":1647117084159,"task_location":{},"stopped":"2874274324586996059"}}
""".trimIndent()


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
            ActiveTask(response(jsons.first().deserialize<Named<TimeEntry>>().value))
        }

        console.log(root.innerHTML)

        root.innerHTML shouldContain "12.03.2022"
    }
}
