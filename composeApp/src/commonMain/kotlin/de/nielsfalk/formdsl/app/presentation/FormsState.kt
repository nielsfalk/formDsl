package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.misc.FormsListItem

data class FormsState(
    val availableForms: List<FormsListItem> = emptyList(),
    val selectedForm: String? = null,
    val loading: Boolean = false
)
