package foo.bar.n8.ui.screens.spacelaunch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.DomainError
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.State
import foo.bar.clean.domain.features.auth.AuthModel
import foo.bar.clean.domain.features.auth.AuthState
import foo.bar.clean.domain.features.spacelaunch.SpaceDetailModel
import foo.bar.clean.domain.features.spacelaunch.SpaceDetailState
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSpaceLaunchScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun SpaceLaunchDetailScreen(
    launchId: String,
    spaceDetailSsot: ReadableState<SpaceDetailState> = (koinInject() as SpaceDetailModel),
    authStateProvider: ReadableState<AuthState> = (koinInject() as AuthModel),
    actionHandler: ActionHandlerSpaceLaunchScreen = koinInject(),
) {

    LaunchedEffect(Unit) {
        actionHandler.handle(Act.ScreenSpaceLaunch.RefreshLaunchDetail(id = launchId))
    }

    val spaceDetailState by spaceDetailSsot.toState()
    val authState by authStateProvider.toState()

    SpaceLaunchDetailView(
        // we're creating a custom derived viewState here, but no need to, can also just pass in the domain states
        viewState = SpaceDetailViewState(spaceDetailState, authState),
        perform = { action -> actionHandler.handle(action) },
    )
}

/**
 * This viewState is completely optional. It's ephemeral, exists only in the UI layer and is
 * derived from states that exist in the domain layer. The important part is this: our
 * source of truth remains in the domain layer
 */
data class SpaceDetailViewState(
    val spaceDetailState: SpaceDetailState = SpaceDetailState(),
    val auth: AuthState = AuthState(),
) : State {
    val error =
        if (spaceDetailState.error != DomainError.NoError) spaceDetailState.error else auth.error
    val loading = spaceDetailState.loading || auth.loading
    val btnFetchEnabled = !loading
    val btnSignOutEnabled = !loading && auth.signedIn
    val btnSignInEnabled = !loading && !auth.signedIn
    val btnBookEnabled = !loading && auth.signedIn && !spaceDetailState.isBooked
    val btnCancelEnabled = !loading && auth.signedIn && spaceDetailState.isBooked
}
