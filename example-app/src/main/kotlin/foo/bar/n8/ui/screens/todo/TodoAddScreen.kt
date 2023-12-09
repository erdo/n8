package foo.bar.n8.ui.screens.todo

import androidx.compose.runtime.Composable
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerTodoScreen
import org.koin.compose.koinInject

@Composable
fun TodoAddScreen(
    actionHandler: ActionHandlerTodoScreen = koinInject(),
) {
    TodoAddView(
        perform = { action -> actionHandler.handle(action) }
    )
}
