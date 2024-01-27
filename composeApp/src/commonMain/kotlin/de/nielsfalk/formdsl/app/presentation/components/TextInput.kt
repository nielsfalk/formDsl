package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.app.presentation.FormEvent.FormDataChange
import de.nielsfalk.formdsl.dsl.Element.Input.TextInput
import de.nielsfalk.formdsl.misc.FormDataValue.StringValue

@Composable
fun TextInput(
    element: TextInput,
    dataValue: StringValue?,
    onEvent: (FormEvent) -> Unit
) {
    TextField(
        placeholder = element.placeholder?.let { { Text(text = it) } },
        value = dataValue?.value ?: "",
        onValueChange = { onEvent(FormDataChange(element, StringValue(it))) },
        modifier = Modifier.fillMaxSize()
    )
}