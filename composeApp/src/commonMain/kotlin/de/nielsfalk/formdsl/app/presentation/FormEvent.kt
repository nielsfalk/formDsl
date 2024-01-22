package de.nielsfalk.formdsl.app.presentation

sealed interface FormEvent{
    data object ReloadForms: FormEvent
    data object DeselectForm:FormEvent

    data class SelectForm(val formId:String): FormEvent
}