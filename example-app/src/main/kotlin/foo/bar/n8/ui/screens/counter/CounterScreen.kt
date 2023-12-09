package foo.bar.n8.ui.screens.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.counter.CounterModel
import foo.bar.clean.domain.features.counter.CounterState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerCounterScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun CounterScreen(
    counterStateProvider: ReadableState<CounterState> = (koinInject() as CounterModel),
    actionHandler: ActionHandlerCounterScreen = koinInject(),
) {

    val counterState by counterStateProvider.toState()

    CounterView(
        counterState = counterState,
        perform = { action -> actionHandler.handle(action) },
    )
}
