package de.nielsfalk.formdsl.misc

import de.nielsfalk.formdsl.misc.FormDataValue.LongValue
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class FormDataTest : FreeSpec({
    val json = Json {
        encodeDefaults = true
        prettyPrint = true
    }

    val formData = FormData(
        mapOf(
            "foo" to StringValue("foo"),
            "bar" to LongValue(15),
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
                    }
                },
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