package foo.bar.n8.ui.screens.spacelaunch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.State
import foo.bar.clean.domain.features.spacelaunch.SpaceLaunchModel
import foo.bar.clean.domain.features.spacelaunch.SpaceLaunchState
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSpaceLaunchScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun SpaceLaunchScreen(
    spaceLaunchStateProvider: ReadableState<SpaceLaunchState> = (koinInject() as SpaceLaunchModel),
    actionHandler: ActionHandlerSpaceLaunchScreen = koinInject(),
) {

    val spaceLaunchState by spaceLaunchStateProvider.toState()

    LaunchedEffect(Unit) {
        if (spaceLaunchState.launches.isEmpty()) {
            actionHandler.handle(Act.ScreenSpaceLaunch.RefreshLaunches)
        }
    }

    SpaceLaunchView(
        // we're creating a custom derived viewState here, but no need to, can also just pass in the domain states
        viewState = SpaceLaunchViewState(spaceLaunchState),
        perform = { action -> actionHandler.handle(action) },
    )
}

/**
 * This viewState is completely optional. It's ephemeral, exists only in the UI layer and is
 * derived from states that exist in the domain layer. The important part is this: our
 * source of truth remains the models in the domain layer
 */
data class SpaceLaunchViewState(
    val spaceLaunch: SpaceLaunchState = SpaceLaunchState(),
) : State {
    val loading = spaceLaunch.loading
    val hasData = spaceLaunch.launches.isNotEmpty()
    val btnFetchEnabled = !loading
    val btnClearEnabled = !loading && spaceLaunch.launches.isEmpty().not()
}
