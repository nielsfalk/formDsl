package de.nielsfalk.formdsl.app.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.contactscomposemultiplatform.contacts.presentation.components.FormsListItem

@Composable
internal fun FormsListScreen(
    state: FormsState,
    onEvent: (FormEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "moko-mvvm") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(FormEvent.ReloadForms) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    }
                }
            )
        },
        content = { paddingValues ->

            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colors.secondary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.availableForms) { form ->
                        FormsListItem(
                            formName = form.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(FormEvent.SelectForm(form.id))
                                }
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    )
}
