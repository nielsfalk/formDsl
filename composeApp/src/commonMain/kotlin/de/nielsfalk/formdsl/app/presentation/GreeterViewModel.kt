package de.nielsfalk.formdsl.app.presentation

class GreeterViewModel(
    private val state: GreeterState,
    private val setState: (GreeterState) -> Unit
) {
    fun toggleShowContent(){
        setState(state.copy(showContent = !state.showContent))
    }
}