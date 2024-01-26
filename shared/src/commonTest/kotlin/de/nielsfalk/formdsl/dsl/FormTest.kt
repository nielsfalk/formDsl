package de.nielsfalk.formdsl.dsl

import de.nielsfalk.jsonUtil.defaultJson
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FormTest : FreeSpec({
    val json = Json {
        defaultJson()
        prettyPrint = true
    }

    "serialize form" {
        val form = form {
            id = "65ad3b7099fcc259a59a4f58"
            title = "a noodle survey"
            textInput {
                description = "aRootElement"
            }
            textInput {
                description = "anotherRootElementWithNextGeneratedId"
            }
            booleanInput { }
            section {
                label("select a date")
                textInput {
                    placehoder = "a placeholder"
                    description = "a textInput description"
                }

            }
            section {
                id = "aSectionId"
                selectMulti {
                    description = "a selectMulti description"
                    option("foo")
                    option("bar")
                }
                selectOne {
                    id = "aTextInputId"
                    defaultValue = "foo"
                    description = "a selectOne description"
                    option("foo", "foo")
                    option("bar", "bar")
                }
                selectMulti {
                    defaultValue = "2020-08-31".toLocalDate()
                    option("2020-08-30".toLocalDate())
                    option("2020-08-31".toLocalDate())
                }
            }
        }

        val serializedForm = json.encodeToString(form)

        //language=JSON
        serializedForm shouldBe """
            {
                "id": "65ad3b7099fcc259a59a4f58",
                "title": "a noodle survey",
                "sections": [
                    {
                        "id": "defaultSection",
                        "elements": [
                            {
                                "type": "TextInput",
                                "id": "textInput0",
                                "description": "aRootElement"
                            },
                            {
                                "type": "TextInput",
                                "id": "textInput1",
                                "description": "anotherRootElementWithNextGeneratedId"
                            },
                            {
                                "type": "BooleanInput",
                                "id": "textInput2"
                            }
                        ]
                    },
                    {
                        "id": "section0",
                        "elements": [
                            {
                                "type": "Label",
                                "content": "select a date"
                            },
                            {
                                "type": "TextInput",
                                "id": "section0-textInput0",
                                "description": "a textInput description",
                                "placeholder": "a placeholder"
                            }
                        ]
                    },
                    {
                        "id": "aSectionId",
                        "elements": [
                            {
                                "type": "SelectMulti",
                                "id": "aSectionId-selectMulti0",
                                "options": [
                                    {
                                        "value": {
                                            "type": "String",
                                            "value": "foo"
                                        }
                                    },
                                    {
                                        "value": {
                                            "type": "String",
                                            "value": "bar"
                                        }
                                    }
                                ],
                                "description": "a selectMulti description"
                            },
                            {
                                "type": "SelectOne",
                                "id": "aTextInputId",
                                "options": [
                                    {
                                        "label": {
                                            "content": "foo"
                                        },
                                        "value": {
                                            "type": "String",
                                            "value": "foo"
                                        }
                                    },
                                    {
                                        "label": {
                                            "content": "bar"
                                        },
                                        "value": {
                                            "type": "String",
                                            "value": "bar"
                                        }
                                    }
                                ],
                                "description": "a selectOne description",
                                "defaultValue": {
                                    "type": "String",
                                    "value": "foo"
                                }
                            },
                            {
                                "type": "SelectMulti",
                                "id": "aSectionId-selectMulti1",
                                "options": [
                                    {
                                        "value": {
                                            "type": "LocalDate",
                                            "value": "2020-08-30"
                                        }
                                    },
                                    {
                                        "value": {
                                            "type": "LocalDate",
                                            "value": "2020-08-31"
                                        }
                                    }
                                ],
                                "defaultValue": {
                                    "value": [
                                        {
                                            "type": "LocalDate",
                                            "value": "2020-08-31"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
    }
})