package foo.bar.n8.ui.screens.favourite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.favourites.FavouritesModel
import foo.bar.clean.domain.features.favourites.FavouritesState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerFavouritesScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun FavouritesScreen(
    favouritesStateProvider: ReadableState<FavouritesState> = (koinInject() as FavouritesModel),
    actionHandler: ActionHandlerFavouritesScreen = koinInject(),
) {

    val favouritesState by favouritesStateProvider.toState()

    FavouritesView(
        favouritesState = favouritesState,
        perform = { action -> actionHandler.handle(action) },
    )
}
