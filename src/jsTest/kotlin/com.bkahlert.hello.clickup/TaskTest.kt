package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.BasicSerializerTest
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.NamedSerializer
import kotlinx.serialization.builtins.ListSerializer

class TaskTest : BasicSerializerTest<Named<List<Task>>>(NamedSerializer(ListSerializer(Task.serializer())),
    // language=JSON
    """
    {
        "tasks": [
            {
                "id": "1yt2xek",
                "custom_id": null,
                "name": "Delete spam automatically",
                "text_content": null,
                "description": null,
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "type": "custom",
                    "orderindex": 1
                },
                "orderindex": "195.00015950000000000000000000000000",
                "date_created": "1645114194584",
                "date_updated": "1645121102051",
                "date_closed": null,
                "archived": false,
                "creator": {
                    "id": 4687596,
                    "username": "Björn Kahlert",
                    "color": "#4169E1",
                    "email": "mail@bkahlert.com",
                    "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
                },
                "assignees": [
                    {
                        "id": 4687596,
                        "username": "Björn Kahlert",
                        "color": "#4169E1",
                        "initials": "BK",
                        "email": "mail@bkahlert.com",
                        "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
                    }
                ],
                "watchers": [],
                "checklists": [],
                "tags": [],
                "parent": null,
                "priority": null,
                "due_date": null,
                "start_date": null,
                "points": null,
                "time_estimate": null,
                "time_spent": 6884795,
                "custom_fields": [
                    {
                        "id": "b2461142-f074-435d-bc08-3258baaf0610",
                        "name": "Completeness",
                        "type": "automatic_progress",
                        "type_config": {
                            "tracking": {
                                "subtasks": true,
                                "checklists": true,
                                "assigned_comments": true
                            },
                            "complete_on": 3,
                            "subtask_rollup": false
                        },
                        "date_created": "1595291046029",
                        "hide_from_guests": false,
                        "value": {
                            "percent_complete": 0
                        },
                        "required": null
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {},
                        "date_created": "1595178997917",
                        "hide_from_guests": false,
                        "required": null
                    },
                    {
                        "id": "7552d46b-3be4-447b-942c-d7eea15c0f9d",
                        "name": "Value",
                        "type": "emoji",
                        "type_config": {
                            "count": 5,
                            "code_point": "2b50"
                        },
                        "date_created": "1595287633290",
                        "hide_from_guests": false,
                        "required": null
                    },
                    {
                        "id": "2a861ab6-b21f-402c-bf21-f0b463df74f1",
                        "name": "days left",
                        "type": "formula",
                        "type_config": {
                            "simple": false,
                            "formula": "IF(SUM(1, DAYS(TASK_DUE_DATE,TODAY())), CONCATENATE(SUM(1, DAYS(TASK_DUE_DATE,TODAY())), \" d\"), \"N/A\")"
                        },
                        "date_created": "1595649126918",
                        "hide_from_guests": false,
                        "value": "N/A",
                        "required": false
                    }
                ],
                "dependencies": [],
                "linked_tasks": [],
                "team_id": "2576831",
                "url": "https://app.clickup.com/t/1yt2xek",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
                    "access": true
                },
                "project": {
                    "id": "11087491",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "folder": {
                    "id": "11087491",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "1zfu735",
                "custom_id": null,
                "name": "Automatisches Einchecken von Test-Nutzern",
                "text_content": null,
                "description": null,
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "type": "open",
                    "orderindex": 0
                },
                "orderindex": "189.00006800000000000000000000000000",
                "date_created": "1641465410256",
                "date_updated": "1641465413976",
                "date_closed": null,
                "archived": false,
                "creator": {
                    "id": 4687596,
                    "username": "Björn Kahlert",
                    "color": "#4169E1",
                    "email": "mail@bkahlert.com",
                    "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
                },
                "assignees": [
                    {
                        "id": 4687596,
                        "username": "Björn Kahlert",
                        "color": "#4169E1",
                        "initials": "BK",
                        "email": "mail@bkahlert.com",
                        "profilePicture": "https://attachments.clickup.com/profilePictures/4687596_ARW.jpg"
                    }
                ],
                "watchers": [],
                "checklists": [],
                "tags": [],
                "parent": null,
                "priority": null,
                "due_date": null,
                "start_date": null,
                "points": null,
                "time_estimate": null,
                "custom_fields": [
                    {
                        "id": "b2461142-f074-435d-bc08-3258baaf0610",
                        "name": "Completeness",
                        "type": "automatic_progress",
                        "type_config": {
                            "tracking": {
                                "subtasks": true,
                                "checklists": true,
                                "assigned_comments": true
                            },
                            "complete_on": 3,
                            "subtask_rollup": false
                        },
                        "date_created": "1595291046029",
                        "hide_from_guests": false,
                        "value": {
                            "percent_complete": 0
                        },
                        "required": null
                    },
                    {
                        "id": "2a861ab6-b21f-402c-bf21-f0b463df74f1",
                        "name": "days left",
                        "type": "formula",
                        "type_config": {
                            "simple": false,
                            "formula": "IF(SUM(1, DAYS(TASK_DUE_DATE,TODAY())), CONCATENATE(SUM(1, DAYS(TASK_DUE_DATE,TODAY())), \" d\"), \"N/A\")"
                        },
                        "date_created": "1595649126918",
                        "hide_from_guests": false,
                        "value": "N/A",
                        "required": false
                    }
                ],
                "dependencies": [],
                "linked_tasks": [],
                "team_id": "2576831",
                "url": "https://app.clickup.com/t/1zfu735",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
                    "access": true
                },
                "project": {
                    "id": "13084360",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "folder": {
                    "id": "13084360",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4565284"
                }
            }
        ]
    }
    """.trimIndent())
