package foo.bar.n8.ui.screens.fruit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.fruit.FruitModel
import foo.bar.clean.domain.features.fruit.FruitState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerFruitScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun FruitScreen(
    fruitStateProvider: ReadableState<FruitState> = (koinInject() as FruitModel),
    actionHandler: ActionHandlerFruitScreen = koinInject(),
) {

    val fruitState by fruitStateProvider.toState()

    FruitView(
        fruitState = fruitState,
        perform = { action -> actionHandler.handle(action) },
    )
}
