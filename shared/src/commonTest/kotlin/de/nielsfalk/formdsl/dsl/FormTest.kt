package de.nielsfalk.formdsl.dsl

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FormTest:FreeSpec({
    val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    "serialize form"{
        val form = form {
            id = "65ad3b7099fcc259a59a4f58"
            title = "a noodle survey"
            section {
                label("select a date")
                selectMulti {
                    option("foo", "foo")
                    option("bar", "bar")
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
                        "elements": [
                            {
                                "type": "Label",
                                "content": "select a date"
                            },
                            {
                                "type": "SelectMulti",
                                "options": [
                                    {
                                        "label": {
                                            "content": "foo"
                                        },
                                        "value": "foo"
                                    },
                                    {
                                        "label": {
                                            "content": "bar"
                                        },
                                        "value": "bar"
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
    }
})