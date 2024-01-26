package de.nielsfalk.formdsl.misc

import de.nielsfalk.formdsl.misc.FormDataValue.ListValue
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

    @SerialName("List")
    @Serializable
    data class ListValue(val value: List<String>) : FormDataValue

    @SerialName("Long")
    @Serializable
    data class LongValue(val value: Long) : FormDataValue

    @SerialName("Boolean")
    @Serializable
    data class BooleanValue(val value: Boolean) : FormDataValue
}

fun ListValue?.toggle(value: String): ListValue =
    set(value, this?.value?.contains(value) != true)

fun ListValue?.set(value: String, add:Boolean): ListValue =
    this?.copy(
        value = if (add)
            this.value + value
        else this.value - value
    )
        ?: ListValue(listOf(value))