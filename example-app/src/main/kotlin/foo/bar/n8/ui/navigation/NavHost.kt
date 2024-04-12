package foo.bar.n8.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import co.early.n8.NavigationState
import co.early.n8.compose.N8Host

@Composable
fun Activity.NavHost(
    content: @Composable (NavigationState<Location, TabHostId>) -> Unit,
) {
    this.N8Host(content = content)
}