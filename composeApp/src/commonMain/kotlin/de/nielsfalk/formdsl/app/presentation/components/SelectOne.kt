package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.dsl.SelectOption
import de.nielsfalk.formdsl.misc.FormDataValue

@Composable
fun SelectOne(element: Element.Input.SelectInput.SelectOne, dataValue: FormDataValue?, onEvent: (FormEvent) -> Unit) {
    element.options.forEach {
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = dataValue == it.value,
                    onClick = {
                        onEvent(FormEvent.FormDataChange(element, it.value))
                    }
                )
                .padding(horizontal = 5.dp)
        ) {
            RadioButton(
                selected = dataValue == it.value,
                onClick = {
                    onEvent(FormEvent.FormDataChange(element, it.value))
                },
                modifier = Modifier.align(Alignment.CenterVertically).size(21.dp)

            )
            Text(
                text = it.text,
                modifier = Modifier.align(Alignment.CenterVertically).padding(5.dp)
            )
        }
    }
}

val SelectOption.text: String
    get() = label?.content ?: value.toString()