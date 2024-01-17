package de.nielsfalk.formdsl.app.presentation

sealed interface FormEvent{
    object ReloadForms: FormEvent

    data class SelectForm(val name:String): FormEvent
}