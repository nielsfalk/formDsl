package de.nielsfalk.formdsl.dsl

import kotlinx.serialization.Serializable

@Serializable
data class FormData(
    val version: Long = 0,
    val values: Map<String, FormDataValue>
)

@Serializable
data class FormDataValue(val string: String? = null, val long: Long? = null)