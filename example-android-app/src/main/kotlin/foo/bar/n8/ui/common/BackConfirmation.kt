import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import co.early.n8.NavigationState
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun backInterceptor(): suspend (NavigationState<Location, TabHostId>) -> Boolean {

    var continuation = remember { mutableStateOf<CancellableContinuation<Boolean>?>(null) }

    if (continuation.value != null) {
        BackConfirmation(
            onResume = { value ->
                continuation.value?.safeResume(value)
                continuation.value = null
            }
        )
    }

    return { statePreBack ->
        if (statePreBack.canNavigateBack) {
            false
        } else {
            suspendCancellableCoroutine { cont ->
                continuation.value = cont
            }
        }
    }
}

@Composable
fun BackConfirmation(
    onResume: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onResume(true) },
        title = { Text("Confirmation") },
        text = { Text("N8 lets you intercept any back operation before it happens") },
        confirmButton = {
            TextButton(onClick = { onResume(false) }) {
                Text("Quit")
            }
        },
        dismissButton = {
            TextButton(onClick = { onResume(true) }) {
                Text("Cancel")
            }
        }
    )
}

private fun CancellableContinuation<Boolean>.safeResume(value: Boolean) {
    if (isActive) {
        resume(value) { _, _, _ -> }
    }
}
