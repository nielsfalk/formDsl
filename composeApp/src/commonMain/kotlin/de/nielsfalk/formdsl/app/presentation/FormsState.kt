package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormsListItem

data class FormsState(
    val availableForms: List<FormsListItem> = emptyList(),
    val selectedForm: Form? = null,
    val loading: Boolean = false
)
