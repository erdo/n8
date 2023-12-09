package foo.bar.n8.ui.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import co.early.fore.compose.observeAsState
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import org.koin.compose.koinInject

val LocalNavHost =
    compositionLocalOf<NavigationState> { error("To access LocalNavHost, your compose code must be wrapped in a NavHost{} block, we'd suggest somewhere high up in the UI tree/hierarchy, just inside setContent{}") }

/**
 * Top level navigation container for the app, anything wrapped inside this element will receive the
 * current page for rendering
 */
@Composable
fun Activity.NavHost(
    navModel: NavigationModel = koinInject(),
    content: @Composable (NavigationState) -> Unit,
) {

    val navigationState by navModel.observeAsState { navModel.state }

    BackHandler(navigationState.canNavigateBack()) {
        navModel.popBackStack()
    }

    CompositionLocalProvider(LocalNavHost provides navigationState) {
        content(navigationState)
    }
}
