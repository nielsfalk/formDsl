package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.nielsfalk.formdsl.dsl.Element

@Composable
fun FormElement(element: Element) {
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

        is Element.Input.TextInput -> TODO()
    }
}