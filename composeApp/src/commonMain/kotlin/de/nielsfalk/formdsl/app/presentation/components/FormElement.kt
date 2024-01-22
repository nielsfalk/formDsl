package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.misc.FormDataValue

@Composable
fun FormElement(
    element: Element,
    values: Map<String, FormDataValue>,
    onEvent: (FormEvent) -> Unit
) {
    when (element) {
        is Element.Label -> Text(
            text = element.content,
        )

        is Element.Input ->
            InputElement(element, values[element.id], onEvent)
    }
}

