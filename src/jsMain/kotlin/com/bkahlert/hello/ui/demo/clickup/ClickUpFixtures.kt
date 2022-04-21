package com.bkahlert.hello.ui.demo.clickup

import com.bkahlert.hello.deserialize
import com.bkahlert.hello.plugins.clickup.Pomodoro
import com.bkahlert.hello.ui.demo.ImageFixtures
import com.bkahlert.kommons.time.Now
import com.bkahlert.kommons.time.minus
import com.bkahlert.kommons.time.minutes
import com.clickup.api.Folder
import com.clickup.api.Space
import com.clickup.api.Task
import com.clickup.api.TaskList
import com.clickup.api.Team
import com.clickup.api.TimeEntry
import com.clickup.api.User
import kotlin.js.Date

object ClickUpFixtures {
    val UserJson = """
        {
            "id": 11111,
            "username": "john.doe",
            "email": "john.doe@example.com",
            "color": "#ff0000",
            "profilePicture": "${ImageFixtures.JohnDoe}",
            "initials": "JD",
            "week_start_day": 1,
            "global_font_support": false,
            "timezone": "Europe/Berlin"
        }
        """.trimIndent()

    val User: User by lazy { UserJson.deserialize() }
    val Teams: List<Team> by lazy {
        """
        [
            {
                "id": "1111111",
                "name": "Pear",
                "color": "#00ff00",
                "avatar": "${ImageFixtures.PearLogo.dataURI}",
                "members": [
                    {
                        "user": $UserJson
                    }
                ]
            },
            {
                "id": "2222222",
                "name": "Kommons",
                "color": "#0000ff",
                "avatar": "${ImageFixtures.KommonsLogo.dataURI}",
                "members": [
                    {
                        "user": $UserJson
                    }
                ]
            }
        ]
        """.trimIndent().deserialize()
    }
    val Tasks: List<Task> by lazy {
        """
        [
            {
                "id": "20jg1er",
                "name": "hello.bkahlert.com",
                "text_content": "",
                "description": "",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 249.0026489,
                "date_created": 1647040375239,
                "date_updated": 1647537376018,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
                "time_estimate": 864000000,
                "time_spent": 300501939,
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/20jg1er",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
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
                "id": "1yt2xek",
                "name": "Delete spam automatically",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 199.0003736,
                "date_created": 1645114194584,
                "date_updated": 1647040454344,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
                "time_spent": 6890666,
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1yt2xek",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
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
                "name": "Automatisches Einchecken von Test-Nutzern",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 189.000068,
                "date_created": 1641465410256,
                "date_updated": 1641465413976,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zfu735",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zftxd7",
                "name": "Test-Datengenerierung QA",
                "text_content": "DKB - BayernLB - Sparkassenverbund\n\nReview / DEV / Int \nAlte und neue Banking API\nAlt:\nMock durch FI\nNeu:\nMock durch DKB\n\nRepo:\nDatenquelle für alten und neuen Mock\nOnline-Tool zum Erstellung der Mock-Daten\n\nDaten:\nCash-Account\nDebit-Card\n\nQA\nZiel\nAnlage echter User (Testen)\nLöschen existierender User\nAudit\nWeitere Daten wie Depots\nGemeinsame Anlage\n10.000 / 30.000",
                "description": "DKB - BayernLB - Sparkassenverbund\n\nReview / DEV / Int \nAlte und neue Banking API\nAlt:\nMock durch FI\nNeu:\nMock durch DKB\n\nRepo:\nDatenquelle für alten und neuen Mock\nOnline-Tool zum Erstellung der Mock-Daten\n\nDaten:\nCash-Account\nDebit-Card\n\nQA\nZiel\nAnlage echter User (Testen)\nLöschen existierender User\nAudit\nWeitere Daten wie Depots\nGemeinsame Anlage\n10.000 / 30.000",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 186.12704344911424,
                "date_created": 1641463760120,
                "date_updated": 1641464887333,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zftxd7",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zftw92",
                "name": "Link Teams-Channel",
                "text_content": "",
                "description": "",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 176.3204338420263,
                "date_created": 1641463479154,
                "date_updated": 1641465306031,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zftw92",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zftw7j",
                "name": "PACT",
                "text_content": "Vorbehalte durch Teams\nLead: Jan Klug\n\nZiel: PACT loswerden\nMaximal noch Unterstützung bei Einleitung\nMatthias Thiele: Befürworter",
                "description": "Vorbehalte durch Teams\nLead: Jan Klug\n\nZiel: PACT loswerden\nMaximal noch Unterstützung bei Einleitung\nMatthias Thiele: Befürworter",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 183.0001097,
                "date_created": 1641463459596,
                "date_updated": 1641463614449,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zftw7j",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zftj9c",
                "name": "Get Access data",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 177.91026477101315,
                "date_created": 1641460866653,
                "date_updated": 1641460870482,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zftj9c",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zftj7t",
                "name": "Link list - Utku",
                "text_content": "https://confluence.dkb.ag/display/TEGU \nhttps://confluence.dkb.ag/pages/viewpage.action?pageId=139339100",
                "description": "https://confluence.dkb.ag/display/TEGU \nhttps://confluence.dkb.ag/pages/viewpage.action?pageId=139339100",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 179.0000957,
                "date_created": 1641460855822,
                "date_updated": 1641465689260,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zftj7t",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "1zfthxd",
                "name": "Introduce to new colleague: Florian Schmidt",
                "text_content": "",
                "description": "",
                "status": {
                    "status": "to do",
                    "color": "#bf55ec",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 176.8204338420263,
                "date_created": 1641460769804,
                "date_updated": 1641461873791,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/1zfthxd",
                "permission_level": "create",
                "list": {
                    "id": "27814619",
                    "name": "Inbox",
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
            },
            {
                "id": "gz9v67",
                "name": "BigNum issue",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 163,
                "date_created": 1618100805929,
                "date_updated": 1618100885206,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/gz9v67",
                "permission_level": "create",
                "list": {
                    "id": "42401635",
                    "name": "Koodies",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "gz9t8v",
                "name": "ScriptSupport",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 161,
                "date_created": 1618095873394,
                "date_updated": 1618095875600,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/gz9t8v",
                "permission_level": "create",
                "list": {
                    "id": "42401635",
                    "name": "Koodies",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "gz9t8b",
                "name": "create script files in .koodies",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 159,
                "date_created": 1618095855861,
                "date_updated": 1618190430602,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
                "time_spent": 72432312,
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/gz9t8b",
                "permission_level": "create",
                "list": {
                    "id": "42401635",
                    "name": "Koodies",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "gz9t7m",
                "name": "resolve build issues IDEA",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 157,
                "date_created": 1618095828989,
                "date_updated": 1618117997826,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
                "time_spent": 17072797,
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/gz9t7m",
                "permission_level": "create",
                "list": {
                    "id": "42401635",
                    "name": "Koodies",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "caw2qt",
                "name": "Evaluate balenaOS",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 158,
                "date_created": 1610299954091,
                "date_updated": 1610344075567,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/caw2qt",
                "permission_level": "create",
                "list": {
                    "id": "40473413",
                    "name": "Bother You?!",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "caw2kb",
                "name": "Evaluate hypriot",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 156,
                "date_created": 1610299879105,
                "date_updated": 1610344051409,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/caw2kb",
                "permission_level": "create",
                "list": {
                    "id": "40473413",
                    "name": "Bother You?!",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "caw2eg",
                "name": "Publish to maven central",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                },
                "orderindex": 154,
                "date_created": 1610299807778,
                "date_updated": 1610299832308,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/caw2eg",
                "permission_level": "create",
                "list": {
                    "id": "42401635",
                    "name": "Koodies",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "c2whgw",
                "name": "Test image",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 153,
                "date_created": 1609785077902,
                "date_updated": 1609785079958,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/c2whgw",
                "permission_level": "create",
                "list": {
                    "id": "40473412",
                    "name": "ImgCstmzr",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "c2whgt",
                "name": "Fix flash",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 152,
                "date_created": 1609785070045,
                "date_updated": 1609785071916,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/c2whgt",
                "permission_level": "create",
                "list": {
                    "id": "40473412",
                    "name": "ImgCstmzr",
                    "access": true
                },
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985"
                }
            },
            {
                "id": "c2wgmp",
                "name": "20 Mails bearbeiten",
                "text_content": "",
                "description": "",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 151,
                "date_created": 1609784824618,
                "date_updated": 1609784824618,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                    {
                        "name": "focus",
                        "tag_fg": "#800000",
                        "tag_bg": "#0231e8",
                        "creator": ${User.id.stringValue}
                    }
                ],
                "due_date": 1596160800000,
                "time_estimate": 1500000,
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/c2wgmp",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
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
                "id": "7cku3f",
                "name": "Build a Raspberry Pi Webcam Server in Minutes | https://pimylifeup.com/raspberry-pi-webcam-server/",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 145,
                "date_created": 1597551981002,
                "date_updated": 1597551981391,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/7cku3f",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
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
                "id": "78mj21",
                "name": "Improve own shell based on https://medium.com/@ivanaugustobd/your-terminal-can-be-much-much-more-productive-5256424658e8",
                "text_content": "https://medium.com/@ivanaugustobd/your-terminal-can-be-much-much-more-productive-5256424658e8\n",
                "description": "https://medium.com/@ivanaugustobd/your-terminal-can-be-much-much-more-productive-5256424658e8\n",
                "status": {
                    "status": "to do",
                    "color": "#02bcd4",
                    "orderindex": 0,
                    "type": "open"
                },
                "orderindex": 144,
                "date_created": 1597094330386,
                "date_updated": 1597094331378,
                "creator": $UserJson,
                "assignees": [
                    $UserJson
                ],
                "watchers": [
                ],
                "checklists": [
                ],
                "tags": [
                ],
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
                        "hide_from_guests": false
                    },
                    {
                        "id": "e5e9419e-dd0b-45cc-aa0e-78f1663db300",
                        "name": "URI",
                        "type": "url",
                        "type_config": {
                        },
                        "date_created": "1595178997917",
                        "hide_from_guests": false
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
                        "hide_from_guests": false
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
                        "required": false
                    }
                ],
                "dependencies": [
                ],
                "linked_tasks": [
                ],
                "team_id": "${Teams.first().id.stringValue}",
                "url": "https://app.clickup.com/t/78mj21",
                "permission_level": "create",
                "list": {
                    "id": "25510969",
                    "name": "Professional",
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
            }
        ]
        """.trimIndent().deserialize()
    }
    val Spaces: List<Space> by lazy {
        """
        [
            {
                "id": "4564985",
                "name": "Björn",
                "private": false,
                "statuses": [
                    {
                        "id": "p4564985_LD0PMLG0",
                        "status": "to do",
                        "color": "#02bcd4",
                        "orderindex": 0,
                        "type": "open"
                    },
                    {
                        "id": "p4564985_BBLbHB8w",
                        "status": "in progress",
                        "color": "#a875ff",
                        "orderindex": 1,
                        "type": "custom"
                    },
                    {
                        "id": "p4564985_BrkjGhEw",
                        "status": "Closed",
                        "color": "#6bc950",
                        "orderindex": 2,
                        "type": "closed"
                    }
                ],
                "multiple_assignees": false
            },
            {
                "id": "4565284",
                "name": "Work",
                "private": false,
                "statuses": [
                    {
                        "id": "p4565284_cjCvFI8U",
                        "status": "to do",
                        "color": "#d3d3d3",
                        "orderindex": 0,
                        "type": "open"
                    },
                    {
                        "id": "p4565284_75Em3AUr",
                        "status": "in progress",
                        "color": "#7c4dff",
                        "orderindex": 1,
                        "type": "custom"
                    },
                    {
                        "id": "p4565284_IqTX5Clr",
                        "status": "complete",
                        "color": "#6bc950",
                        "orderindex": 2,
                        "type": "closed"
                    }
                ],
                "multiple_assignees": false
            }
        ]
        """.trimIndent().deserialize()
    }
    val Space1Folders: List<Folder> by lazy {
        """
        [
            {
                "id": "19413895",
                "name": "Open Source",
                "orderindex": 2,
                "override_statuses": true,
                "hidden": false,
                "task_count": "9",
                "archived": false,
                "statuses": [
                    {
                        "id": "c19413895_K7hd1oi5",
                        "status": "to do",
                        "color": "#02bcd4",
                        "orderindex": 0,
                        "type": "open"
                    },
                    {
                        "id": "c19413895_vBZevWca",
                        "status": "in progress",
                        "color": "#a875ff",
                        "orderindex": 1,
                        "type": "custom"
                    },
                    {
                        "id": "c19413895_0jzPMb7B",
                        "status": "Closed",
                        "color": "#6bc950",
                        "orderindex": 2,
                        "type": "closed"
                    }
                ],
                "lists": [
                    {
                        "id": "40473414",
                        "name": "libguestfs",
                        "orderindex": 0,
                        "task_count": 0,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "permission_level": "create"
                    },
                    {
                        "id": "42401635",
                        "name": "Koodies",
                        "orderindex": 1,
                        "task_count": 5,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "override_statuses": false,
                        "permission_level": "create"
                    },
                    {
                        "id": "40473412",
                        "name": "ImgCstmzr",
                        "orderindex": 2,
                        "task_count": 2,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "permission_level": "create"
                    },
                    {
                        "id": "40473413",
                        "name": "Bother You?!",
                        "orderindex": 3,
                        "task_count": 2,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "permission_level": "create"
                    }
                ],
                "permission_level": "create"
            }
        ]
        """.trimIndent().deserialize()
    }
    val Space2Folders: List<Folder> by lazy {
        """
        []
        """.trimIndent().deserialize()
    }
    val Space1FolderLists: List<TaskList> by lazy {
        """
        [
            {
                "id": "40473414",
                "name": "libguestfs",
                "orderindex": 0,
                "task_count": 0,
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "permission_level": "create"
            },
            {
                "id": "42401635",
                "name": "Koodies",
                "orderindex": 1,
                "task_count": 5,
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "override_statuses": false,
                "permission_level": "create"
            },
            {
                "id": "40473412",
                "name": "ImgCstmzr",
                "orderindex": 2,
                "task_count": 2,
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "permission_level": "create"
            },
            {
                "id": "40473413",
                "name": "Bother You?!",
                "orderindex": 3,
                "task_count": 2,
                "folder": {
                    "id": "19413895",
                    "name": "Open Source",
                    "hidden": false,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "permission_level": "create"
            }
        ]
        """.trimIndent().deserialize()
    }
    val Space2FolderLists: List<TaskList> by lazy {
        """
        []
        """.trimIndent().deserialize()
    }
    val Space1FolderlessLists: List<TaskList> by lazy {
        """
        [
            {
                "id": "25510969",
                "name": "Professional",
                "orderindex": 0,
                "status": {
                    "status": "professsional",
                    "color": "#02bcd4"
                },
                "assignee": $UserJson,
                "task_count": 46,
                "folder": {
                    "id": "11087491",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "override_statuses": false,
                "permission_level": "create"
            },
            {
                "id": "25510968",
                "name": "Private",
                "orderindex": 0,
                "status": {
                    "status": "private",
                    "color": "#2ecd6f"
                },
                "assignee": $UserJson,
                "task_count": 7,
                "folder": {
                    "id": "11087492",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4564985",
                    "name": "Björn",
                    "access": true
                },
                "archived": false,
                "override_statuses": false,
                "permission_level": "create"
            }
        ]
        """.trimIndent().deserialize()
    }
    val Space2FolderlessLists: List<TaskList> by lazy {
        """
        [
            {
                "id": "25511012",
                "name": "DKB",
                "orderindex": 0,
                "status": {
                    "status": "dkb",
                    "color": "#04a9f4"
                },
                "assignee": $UserJson,
                "task_count": 0,
                "folder": {
                    "id": "11067957",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4565284",
                    "name": "Work",
                    "access": true
                },
                "archived": false,
                "override_statuses": true,
                "permission_level": "create"
            },
            {
                "id": "25511036",
                "name": "Senacor",
                "orderindex": 1,
                "status": {
                    "status": "senacor",
                    "color": "#1bbc9c"
                },
                "assignee": $UserJson,
                "task_count": 16,
                "folder": {
                    "id": "11067967",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4565284",
                    "name": "Work",
                    "access": true
                },
                "archived": false,
                "override_statuses": true,
                "permission_level": "create"
            },
            {
                "id": "27814619",
                "name": "Inbox",
                "orderindex": 0,
                "status": {
                    "status": "inbox",
                    "color": "#bf55ec"
                },
                "task_count": 7,
                "folder": {
                    "id": "13084360",
                    "name": "hidden",
                    "hidden": true,
                    "access": true
                },
                "space": {
                    "id": "4565284",
                    "name": "Work",
                    "access": true
                },
                "archived": false,
                "override_statuses": true,
                "permission_level": "create"
            }
        ]
        """.trimIndent().deserialize()
    }
    val TimeEntry: TimeEntry by lazy {
        """
        {
            "id": "2876109920259311585",
            "task": {
                "id": "20jg1er",
                "name": "hello.bkahlert.com",
                "status": {
                    "status": "in progress",
                    "color": "#a875ff",
                    "orderindex": 1,
                    "type": "custom"
                }
            },
            "wid": "${Teams.first().id.stringValue}",
            "user": $UserJson,
            "billable": false,
            "start": 1647225649667,
            "duration": -350610,
            "description": "50m pomodoro",
            "tags": [
                {
                    "name": "pomodoro",
                    "tag_fg": "#ff6347",
                    "tag_bg": "#ff6347",
                    "creator": ${User.id.stringValue}
                }
            ],
            "source": "api",
            "at": 1647225649667,
            "task_url": "https://app.clickup.com/t/20jg1er"
        }
        """.trimIndent().deserialize()
    }

    fun TimeEntry.running(
        start: Date = Now - 3.5.minutes,
        type: Pomodoro.Type? = null,
    ) = copy(
        start = start,
        tags = type?.addTag(tags) ?: tags
    )

    fun TimeEntry.aborted(
        start: Date = Now - Pomodoro.Type.Default.duration / 2,
        end: Date = Now,
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ) = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )

    fun TimeEntry.completed(
        start: Date = Now - Pomodoro.Type.Default.duration,
        end: Date = Now,
        type: Pomodoro.Type? = Pomodoro.Type.Default,
    ) = copy(
        start = start,
        end = end,
        tags = type?.addTag(tags) ?: tags
    )
}
