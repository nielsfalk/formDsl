import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.nielsfalk.formdsl.app.data.FormsRepository
import de.nielsfalk.formdsl.app.presentation.FormScreen
import de.nielsfalk.formdsl.app.presentation.FormsListScreen
import de.nielsfalk.formdsl.app.presentation.FormsState
import de.nielsfalk.formdsl.app.presentation.FormsViewModel
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
            FormScreen(it, viewModel::onEvent)
        }
            ?: FormsListScreen(state, viewModel::onEvent)
    }
}
