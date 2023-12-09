package foo.bar.n8.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import co.early.n8.NavigationModel
import co.early.n8.NavigationState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerNavigationScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun NavigationScreen(
    navigationStateProvider: ReadableState<NavigationState> = (koinInject() as NavigationModel),
    actionHandler: ActionHandlerNavigationScreen = koinInject(),
) {

    val navigationState by navigationStateProvider.toState()

    NavigationView(
        viewState = navigationState,
        perform = { action -> actionHandler.handle(action) },
    )
}
