package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput

@Composable
fun FormElement(
    element: Element,
    onEvent: (FormEvent) -> Unit
) {
    when (element) {
        is Element.Label -> Text(
            text = element.content,
        )

        is Element.Input.SelectInput.SelectMulti -> Text(
            text = "select multi",
        )

        is Element.Input.SelectInput.SelectOne -> Text(
            text = "select one",
        )

        is TextInput -> TextField(
            value = "${element.placeholder}",
            onValueChange = { onEvent(FormEvent.FormDataChange(element, it)) }
        )
    }
}