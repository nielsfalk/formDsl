package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.app.data.FormsRepository
import de.nielsfalk.formdsl.app.presentation.FormEvent.ReloadForms
import de.nielsfalk.formdsl.dsl.Element
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormDataValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FormsViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    private val _state: MutableStateFlow<FormsState> = MutableStateFlow(FormsState())
    val state: StateFlow<FormsState> get() = _state

    init {
        loadAvailableForms()
    }

    fun onEvent(event: FormEvent) {
        when (event) {
            is ReloadForms -> {
                loadAvailableForms()
            }

            is FormEvent.SelectForm -> {
                _state.update { it.copy(loading = true) }
                viewModelScope.launch {
                    val form = repository.getForm(event.formId)
                    _state.update {
                        it.copy(
                            selectedForm = SelectedState(
                                form = form,
                                data = FormData(form.defaultValues())
                            ),
                            loading = false
                        )
                    }
                }
            }

            is FormEvent.DeselectForm -> {
                _state.update {
                    it.copy(selectedForm = null)
                }
            }

            is FormEvent.FormDataChange -> {
                _state.value.selectedForm?.let { selectedForm ->
                    val values = selectedForm.data.values +
                            (event.element.id to event.value)
                    _state.update { it.copy(values = values) }

                    viewModelScope.launch {
                        saveData()
                    }
                }
            }
        }
    }

    private suspend fun saveData() {
        while (_state.value.saving) {
            delay(10)
        }
        _state.value.unsavedData?.let { unsaved ->
            _state.update { it.saving() }
            if (unsaved.dataId == null) {
                val dataId = repository.create(unsaved.formId, unsaved.data)
                _state.update { it.successfulCreated(unsaved, dataId) }
            } else {
                val updatedVersion = repository.update(unsaved.formId, unsaved.dataId, unsaved.data)
                _state.update { it.successfulUpdated(unsaved, updatedVersion) }
            }
        }

    }

    private fun loadAvailableForms() {
        _state.update { it.copy(loading = true) }
        viewModelScope.launch {
            val forms = repository.getAvailableForms()
            _state.update {
                it.copy(
                    availableForms = forms,
                    loading = false
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}

private fun Form.defaultValues(): Map<String, FormDataValue> =
    sections.flatMap { it.elements }
        .mapNotNull { it as? Element.Input }
        .filter { it.defaultValue != null }
        .mapNotNull { it.defaultValue?.run { it.id to this } }
        .toMap()