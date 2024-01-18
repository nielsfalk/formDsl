package de.nielsfalk.formdsl

import kotlinx.serialization.Serializable

@Serializable
data class Form(
    val name: String
)

val allForms = listOf(
    Form("foo"),
    Form("bar")
)
