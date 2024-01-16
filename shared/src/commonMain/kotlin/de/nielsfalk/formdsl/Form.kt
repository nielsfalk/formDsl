package de.nielsfalk.formdsl

data class Form(
    val name: String
)

val allForms = listOf(
    Form("foo"),
    Form("bar")
)
