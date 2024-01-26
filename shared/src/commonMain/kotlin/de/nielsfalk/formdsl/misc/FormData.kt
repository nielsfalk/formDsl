package de.nielsfalk.formdsl.misc

import getPlatform
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FormData(
    val values: Map<String, FormDataValue>,
    val platform: String = getPlatform().name,
    val version: Long = 0
)

@Serializable
sealed interface FormDataValue {

    @SerialName("String")
    @Serializable
    data class StringValue(val value: String) : FormDataValue

    @SerialName("Long")
    @Serializable
    data class LongValue(val value: Long) : FormDataValue

    @SerialName("Boolean")
    @Serializable
    data class BooleanValue(val value: Boolean) : FormDataValue
}