package foo.bar.n8.ui.screens.settings

import androidx.compose.runtime.Composable
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSettingsScreen
import org.koin.compose.koinInject

@Composable
fun SetColorScreen(
    actionHandler: ActionHandlerSettingsScreen = koinInject(),
) {

    SetColorView(
        perform = { action -> actionHandler.handle(action) },
    )
}
