package de.nielsfalk.formdsl.dsl

import kotlinx.serialization.Serializable

@Serializable
data class FormData(
    val values: Map<String, FormDataValue>
)

@Serializable
data class FormDataValue(val string: String? = null, val long: Long? = null)