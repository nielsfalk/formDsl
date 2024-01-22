package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.dsl.Element

sealed interface FormEvent{
    data object ReloadForms: FormEvent
    data object DeselectForm:FormEvent
    data class SelectForm(val formId:String): FormEvent
    data class FormDataChange(val element: Element, val value: String): FormEvent
}