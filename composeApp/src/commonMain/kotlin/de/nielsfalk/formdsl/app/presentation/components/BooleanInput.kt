package de.nielsfalk.formdsl.app.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nielsfalk.formdsl.app.presentation.FormEvent
import de.nielsfalk.formdsl.app.presentation.FormEvent.FormDataChange
import de.nielsfalk.formdsl.dsl.Element.Input.BooleanInput
import de.nielsfalk.formdsl.misc.FormDataValue
import de.nielsfalk.formdsl.misc.FormDataValue.BooleanValue

@Composable
fun BooleanInput(
    element: BooleanInput,
    dataValue: FormDataValue?,
    onEvent: (FormEvent) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = (dataValue as? BooleanValue)?.value == true,
                onClick = { onEvent(FormDataChange(element, (dataValue as? BooleanValue)?.value != true)) }
            )
            .padding(horizontal = 5.dp)
    ) {
        Switch(
            checked = (dataValue as? BooleanValue)?.value == true,
            onCheckedChange = { onEvent(FormDataChange(element, it)) },
            modifier = Modifier
                .align(CenterVertically)
                .size(21.dp)
                .padding(start = 15.dp)
        )
        Text(
            text = element.description ?: "",
            modifier = Modifier
                .align(CenterVertically)
                .padding(start = 25.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
        )
    }
}