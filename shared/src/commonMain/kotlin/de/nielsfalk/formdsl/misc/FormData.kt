package de.nielsfalk.formdsl.misc

import de.nielsfalk.formdsl.misc.FormDataValue.*
import getPlatform
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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
    val value: Any

    @SerialName("String")
    @Serializable
    data class StringValue(override val value: String) : FormDataValue

    @SerialName("List")
    @Serializable
    data class ListValue(override val value: List<FormDataValue>) : FormDataValue

    @SerialName("Long")
    @Serializable
    data class LongValue(override val value: Long) : FormDataValue

    @SerialName("Boolean")
    @Serializable
    data class BooleanValue(override val value: Boolean) : FormDataValue

    @SerialName("LocalDate")
    @Serializable
    data class LocalDateValue(override val value: LocalDate) : FormDataValue

    @SerialName("LocalDateTime")
    @Serializable
    data class LocalDateTimeValue(override val value: LocalDateTime) : FormDataValue
    companion object
}

fun Companion.of(defaultValue: Any): FormDataValue =
    when (defaultValue) {
        is FormDataValue -> defaultValue
        is String -> StringValue(defaultValue)
        is Boolean -> BooleanValue(defaultValue)
        is LocalDateTime -> LocalDateTimeValue(defaultValue)
        is LocalDate -> LocalDateValue(defaultValue)
        is Long -> LongValue(defaultValue)
        is List<*> -> ListValue(defaultValue.mapNotNull { it?.let(Companion::of) })
        else -> throw Exception("can't transform $defaultValue to FormDataValue?")
    }

fun BooleanValue?.isTrue() =
    this?.value == true

fun BooleanValue?.toggle() =
    BooleanValue(!isTrue())

fun ListValue?.toggle(value: FormDataValue): ListValue =
    set(value, this?.value?.contains(value) != true)

fun ListValue?.set(value: FormDataValue, selected: Boolean): ListValue =
    this?.copy(
        value = if (selected)
            this.value + value
        else this.value - value
    )
        ?: ListValue(if (selected) listOf(value) else emptyList())