package de.nielsfalk.formdsl.app.presentation

data class FormsState(
    val availableForms: List<String> = emptyList(),
    val selectedForm: String? = null,
    val loading: Boolean = false
)
