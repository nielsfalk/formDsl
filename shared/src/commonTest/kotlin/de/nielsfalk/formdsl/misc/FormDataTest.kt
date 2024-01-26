package de.nielsfalk.formdsl.misc

import de.nielsfalk.formdsl.misc.FormDataValue.LongValue
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue
import de.nielsfalk.jsonUtil.defaultJson
import getPlatform
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class FormDataTest : FreeSpec({
    val json = Json {
        defaultJson()
        prettyPrint = true
    }

    val formData = FormData(
        mapOf(
            "foo" to StringValue("foo"),
            "bar" to LongValue(15),
            "list" to FormDataValue.of(
                listOf(
                    3L, "fadf",
                    "2020-08-31T17:43".toLocalDateTime(),
                    "2020-08-31".toLocalDate(),
                    null
                )
            )
        )
    )

    //language=JSON
    val jsonString = """
            {
                "values": {
                    "foo": {
                        "type": "String",
                        "value": "foo"
                    },
                    "bar": {
                        "type": "Long",
                        "value": 15
                    },
                    "list": {
                        "type": "List",
                        "value": [
                            {
                                "type": "Long",
                                "value": 3
                            },
                            {
                                "type": "String",
                                "value": "fadf"
                            },
                            {
                                "type": "LocalDateTime",
                                "value": "2020-08-31T17:43"
                            },
                            {
                                "type": "LocalDate",
                                "value": "2020-08-31"
                            }
                        ]
                    }
                },
                "platform": "${getPlatform().name}",
                "version": 0
            }
        """.trimIndent()

    "encode" {
        val encoded = json.encodeToString(formData)

        encoded shouldBe jsonString
    }

    "decode" {
        val decoded = json.decodeFromString<FormData>(jsonString)

        decoded shouldBe formData
    }
})