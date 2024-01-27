package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.dsl.Element.Input
import de.nielsfalk.formdsl.misc.FormDataValue

sealed interface FormEvent {
    data object ReloadForms : FormEvent
    data object DeselectForm : FormEvent
    data class SelectForm(val formId: String) : FormEvent
    data class FormDataChange(val element: Input, val value: FormDataValue) : FormEvent
}