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
import de.nielsfalk.formdsl.dsl.Element.Input.SelectInput.SelectMulti
import de.nielsfalk.formdsl.misc.FormDataValue
import de.nielsfalk.formdsl.misc.FormDataValue.ListValue
import de.nielsfalk.formdsl.misc.set
import de.nielsfalk.formdsl.misc.toggle

@Composable
fun SelectMulti(
    element: SelectMulti,
    dataValue: FormDataValue?,
    onEvent: (FormEvent) -> Unit
) {
    element.options.forEach { option ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (dataValue as? ListValue)?.value?.contains(option.value) == true,
                    onClick = { onEvent(FormDataChange(element, (dataValue as? ListValue).toggle(option.value))) }
                )
                .padding(horizontal = 5.dp)
        ) {
            Switch(
                checked = (dataValue as? ListValue)?.value?.contains(option.value) == true,
                onCheckedChange = { onEvent(FormDataChange(element, (dataValue as? ListValue).set(option.value, it))) },
                modifier = Modifier
                    .align(CenterVertically)
                    .size(21.dp)
                    .padding(start = 15.dp)
            )
            Text(
                text = option.text,
                modifier = Modifier
                    .align(CenterVertically)
                    .padding(start = 25.dp, top = 5.dp, end = 5.dp, bottom = 5.dp)
            )
        }
    }
}