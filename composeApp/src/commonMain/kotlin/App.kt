import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import de.nielsfalk.formdsl.app.data.FormsRepository
import de.nielsfalk.formdsl.app.presentation.*
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory

@Composable
fun App() {
    MaterialTheme {
        val repository = FormsRepository()
        val viewModel: FormsViewModel = getViewModel(
            key = "formViewModel",
            factory = viewModelFactory { FormsViewModel(repository) }
        )
        val state: FormsState by viewModel.state.collectAsState()

        state.selectedForm?.let {
            FormsScreen(it, viewModel::onEvent)
        }?:
        FormsListScreen(state, viewModel::onEvent)
    }
}
