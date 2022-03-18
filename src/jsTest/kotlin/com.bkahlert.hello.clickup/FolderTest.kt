package com.bkahlert.hello.clickup

import com.bkahlert.kommons.serialization.BasicSerializerTest
import com.bkahlert.kommons.serialization.Named
import com.bkahlert.kommons.serialization.NamedSerializer
import com.clickup.api.Folder
import kotlinx.serialization.builtins.ListSerializer

class FolderTest : BasicSerializerTest<Named<List<Folder>>>(NamedSerializer(ListSerializer(Folder.serializer())),
    // language=JSON
    """
    {
        "folders": [
            {
                "id": "19413895",
                "name": "Open Source",
                "orderindex": 2,
                "override_statuses": true,
                "hidden": false,
                "space": {
                    "id": "4564985",
                    "name": "Björn"
                },
                "task_count": "9",
                "archived": false,
                "statuses": [
                    {
                        "id": "c19413895_K7hd1oi5",
                        "status": "to do",
                        "type": "open",
                        "orderindex": 0,
                        "color": "#02BCD4"
                    },
                    {
                        "id": "c19413895_vBZevWca",
                        "status": "in progress",
                        "type": "custom",
                        "orderindex": 1,
                        "color": "#a875ff"
                    },
                    {
                        "id": "c19413895_0jzPMb7B",
                        "status": "Closed",
                        "type": "closed",
                        "orderindex": 2,
                        "color": "#6bc950"
                    }
                ],
                "lists": [
                    {
                        "id": "40473414",
                        "name": "libguestfs",
                        "orderindex": 0,
                        "status": null,
                        "priority": null,
                        "assignee": null,
                        "task_count": 0,
                        "due_date": null,
                        "start_date": null,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "override_statuses": null,
                        "statuses": [
                            {
                                "id": "c19413895_K7hd1oi5",
                                "status": "to do",
                                "orderindex": 0,
                                "color": "#02BCD4",
                                "type": "open"
                            },
                            {
                                "id": "c19413895_vBZevWca",
                                "status": "in progress",
                                "orderindex": 1,
                                "color": "#a875ff",
                                "type": "custom"
                            },
                            {
                                "id": "c19413895_0jzPMb7B",
                                "status": "Closed",
                                "orderindex": 2,
                                "color": "#6bc950",
                                "type": "closed"
                            }
                        ],
                        "permission_level": "create"
                    },
                    {
                        "id": "42401635",
                        "name": "Koodies",
                        "orderindex": 1,
                        "status": null,
                        "priority": null,
                        "assignee": null,
                        "task_count": 5,
                        "due_date": null,
                        "start_date": null,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "override_statuses": false,
                        "statuses": [
                            {
                                "id": "c19413895_K7hd1oi5",
                                "status": "to do",
                                "orderindex": 0,
                                "color": "#02BCD4",
                                "type": "open"
                            },
                            {
                                "id": "c19413895_vBZevWca",
                                "status": "in progress",
                                "orderindex": 1,
                                "color": "#a875ff",
                                "type": "custom"
                            },
                            {
                                "id": "c19413895_0jzPMb7B",
                                "status": "Closed",
                                "orderindex": 2,
                                "color": "#6bc950",
                                "type": "closed"
                            }
                        ],
                        "permission_level": "create"
                    },
                    {
                        "id": "40473412",
                        "name": "ImgCstmzr",
                        "orderindex": 2,
                        "status": null,
                        "priority": null,
                        "assignee": null,
                        "task_count": 2,
                        "due_date": null,
                        "start_date": null,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "override_statuses": null,
                        "statuses": [
                            {
                                "id": "c19413895_K7hd1oi5",
                                "status": "to do",
                                "orderindex": 0,
                                "color": "#02BCD4",
                                "type": "open"
                            },
                            {
                                "id": "c19413895_vBZevWca",
                                "status": "in progress",
                                "orderindex": 1,
                                "color": "#a875ff",
                                "type": "custom"
                            },
                            {
                                "id": "c19413895_0jzPMb7B",
                                "status": "Closed",
                                "orderindex": 2,
                                "color": "#6bc950",
                                "type": "closed"
                            }
                        ],
                        "permission_level": "create"
                    },
                    {
                        "id": "40473413",
                        "name": "Bother You?!",
                        "orderindex": 3,
                        "status": null,
                        "priority": null,
                        "assignee": null,
                        "task_count": 2,
                        "due_date": null,
                        "start_date": null,
                        "space": {
                            "id": "4564985",
                            "name": "Björn",
                            "access": true
                        },
                        "archived": false,
                        "override_statuses": null,
                        "statuses": [
                            {
                                "id": "c19413895_K7hd1oi5",
                                "status": "to do",
                                "orderindex": 0,
                                "color": "#02BCD4",
                                "type": "open"
                            },
                            {
                                "id": "c19413895_vBZevWca",
                                "status": "in progress",
                                "orderindex": 1,
                                "color": "#a875ff",
                                "type": "custom"
                            },
                            {
                                "id": "c19413895_0jzPMb7B",
                                "status": "Closed",
                                "orderindex": 2,
                                "color": "#6bc950",
                                "type": "closed"
                            }
                        ],
                        "permission_level": "create"
                    }
                ],
                "permission_level": "create"
            }
        ]
    }
    """.trimIndent())
