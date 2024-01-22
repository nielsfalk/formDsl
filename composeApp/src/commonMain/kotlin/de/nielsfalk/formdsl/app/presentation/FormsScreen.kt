package de.nielsfalk.formdsl.app.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.nielsfalk.formdsl.app.presentation.components.FormElement
import de.nielsfalk.formdsl.dsl.Form

@Composable
internal fun FormsScreen(
    form: Form,
    onEvent: (FormEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = form.title) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(FormEvent.DeselectForm) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = { paddingValues ->

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = form.title,
                    )
                }

                items(form.sections.flatMap { it.elements }) { element ->
                    FormElement(element)
                }
            }
        }
    )
}

