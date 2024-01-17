package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.formdsl.app.presentation.FormEvent.ReloadForms
import de.nielsfalk.formdsl.app.data.FormsRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
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
            is FormEvent.SelectForm->{
                println("event = ${event.name}")
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

