package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element.Input
import de.nielsfalk.formdsl.dsl.Element.Input.BooleanInput
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectMulti
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectOne
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.misc.FormDataValue

@Composable
fun InputElement(
    element: Input,
    dataValue: FormDataValue?,
    onEvent: (FormEvent) -> Unit
) {
    if (element !is BooleanInput) {
        element.description?.let { Text(text = it) }
    }
    when (element) {
        is SelectMulti -> SelectMulti(element, dataValue, onEvent)
        is SelectOne -> SelectOne(element, dataValue, onEvent)
        is TextInput -> TextInput(element, dataValue, onEvent)
        is BooleanInput -> BooleanInput(element, dataValue, onEvent)
    }
}