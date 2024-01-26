package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.dsl.Element.Input.BooleanInput
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectOne
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.misc.FormDataValue

@Composable
fun InputElement(element: Element.Input, dataValue: FormDataValue?, onEvent: (FormEvent) -> Unit) {
    if (!(element is BooleanInput)) {
        element.description?.let {
            Text(text = it)
        }
    }
    when (element) {
        is Element.Input.SelectInput.SelectMulti -> Text(
            text = "select multi",
        )

        is SelectOne -> {
            element.options.forEach {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (dataValue as? FormDataValue.StringValue)?.value == it.value,
                            onClick = {
                                onEvent(FormEvent.FormDataChange(element, it.value))
                            }
                        )
                        .padding(horizontal = 5.dp)
                ) {
                    RadioButton(
                        selected = (dataValue as? FormDataValue.StringValue)?.value == it.value,
                        onClick = {
                            onEvent(FormEvent.FormDataChange(element, it.value))
                        },
                        modifier = Modifier.align(CenterVertically).size(21.dp)

                    )
                    Text(
                        text = it.label.content,
                        modifier = Modifier.align(CenterVertically).padding(5.dp)
                    )
                }
            }
        }

        is TextInput -> TextField(
            placeholder = element.placeholder?.let { { Text(text = it) } },
            value = (dataValue as? FormDataValue.StringValue)?.value ?: "",
            onValueChange = { onEvent(FormEvent.FormDataChange(element, it)) }
        )

        is BooleanInput -> {
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (dataValue as? FormDataValue.BooleanValue)?.value == true,
                        onClick = { onEvent(FormEvent.FormDataChange(element, (dataValue as? FormDataValue.BooleanValue)?.value != true)) }
                    )
                    .padding(horizontal = 5.dp)
            ) {
                Switch(
                    checked = (dataValue as? FormDataValue.BooleanValue)?.value == true,
                    onCheckedChange = { onEvent(FormEvent.FormDataChange(element, it)) },
                    modifier = Modifier.align(CenterVertically).size(21.dp).padding(start = 15.dp)
                )
                Text(
                    text = element.description ?: "",
                    modifier = Modifier.align(CenterVertically).padding(start = 25.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
                )
            }
        }
    }
}