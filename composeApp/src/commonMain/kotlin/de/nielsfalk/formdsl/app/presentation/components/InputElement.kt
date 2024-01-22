package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.misc.FormDataValue

@Composable
 fun InputElement(element: Element.Input, dataValue: FormDataValue?, onEvent: (FormEvent) -> Unit) {
    when (element) {
        is Element.Input.SelectInput.SelectMulti -> Text(
            text = "select multi",
        )

        is Element.Input.SelectInput.SelectOne -> Text(
            text = "select one",
        )

        is TextInput -> TextField(
            placeholder = element.placeholder?.let { { Text(text = it) } },
            value = (dataValue as? FormDataValue.StringValue)?.value ?: "",
            onValueChange = { onEvent(FormEvent.FormDataChange(element, it)) }
        )
    }
}