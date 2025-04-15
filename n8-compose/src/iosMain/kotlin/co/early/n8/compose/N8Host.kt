package co.early.n8.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import co.early.fore.compose.observeAsState
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.NavigationState

val LocalN8HostState = compositionLocalOf<NavigationState<*, *>> {
        error("To access LocalN8Host, your compose code must be wrapped in a N8Host<L, T>{} " +
                "block, we'd suggest somewhere high up in the UI tree/hierarchy, just inside " +
                "setContent{}") }

/**
 * Top level navigation container for the app, anything wrapped inside this element will receive the
 * current page for rendering
 */
@Composable @Suppress("FunctionNaming")
fun <L : Any, T : Any> N8Host(
    navigationModel: NavigationModel<L, T> = N8.n8(),
    content: @Composable (NavigationState<L, T>) -> Unit,
) {

    val navigationState by navigationModel.observeAsState { navigationModel.state }

    CompositionLocalProvider(LocalN8HostState provides navigationState) {
        content(navigationState)
    }
}
