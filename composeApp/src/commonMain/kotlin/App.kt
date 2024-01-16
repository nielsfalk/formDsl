import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import de.nielsfalk.formdsl.app.presentation.GreeterScreen
import de.nielsfalk.formdsl.app.presentation.GreeterState
import de.nielsfalk.formdsl.app.presentation.GreeterViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var greeterState by remember { mutableStateOf(GreeterState()) }
        val viewModel = GreeterViewModel(greeterState, setState = { greeterState = it })

        GreeterScreen(greeterState, viewModel::toggleShowContent)
    }
}
