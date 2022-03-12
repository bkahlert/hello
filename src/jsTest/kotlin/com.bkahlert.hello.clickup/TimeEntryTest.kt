package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.BasicSerializerTest
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.NamedSerializer

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
    """.trimIndent())

//
//
//fun <T> response(value: T) = Either.Left<T, Throwable>(value)
//fun <T> failedResponse() = Either.Right<T, Throwable>(ClickUpException(
//    ErrorInfo("something went wrong", "TEST-1234"), RuntimeException("underlying problem")
//))
//
//fun main() {
//
//    // trigger creation to avoid flickering
//    Engine.values().forEach {
//        it.grayscaleImage
//        it.coloredImage
//    }
//
//    renderComposable("root") {
//        Style(AppStylesheet)
//
//        ActiveTask(response<TimeEntry?>(null))
//    }
//}
