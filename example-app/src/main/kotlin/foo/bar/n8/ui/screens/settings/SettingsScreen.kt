package foo.bar.n8.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.State
import foo.bar.clean.domain.features.settings.SettingsModel
import foo.bar.clean.domain.features.settings.SettingsState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSettingsScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    color: ULong? = null,
    settingsStateProvider: ReadableState<SettingsState> = (koinInject() as SettingsModel),
    actionHandler: ActionHandlerSettingsScreen = koinInject(),
) {

    val settingsState by settingsStateProvider.toState()

    SettingsView(
        viewState = SettingsViewState(color, settingsState),
        perform = { action -> actionHandler.handle(action) },
    )
}

/**
 * This viewState is completely optional. It's ephemeral, exists only in the UI layer and is
 * derived from states that exist in the domain layer. The important part is this: our
 * source of truth is still the models in the domain layer
 */
data class SettingsViewState(
    val color: ULong? = null,
    val settingsState: SettingsState = SettingsState(),
) : State
