package foo.bar.n8.ui.screens.todo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import foo.bar.clean.domain.features.ReadableState
import foo.bar.clean.domain.features.todo.TodoModel
import foo.bar.clean.domain.features.todo.TodoState
import foo.bar.clean.ui.actionhandlers.screens.ActionHandlerTodoScreen
import foo.bar.n8.ui.common.toState
import org.koin.compose.koinInject

@Composable
fun TodoEditScreen(
    itemIndex: Int,
    todoStateProvider: ReadableState<TodoState> = (koinInject() as TodoModel),
    actionHandler: ActionHandlerTodoScreen = koinInject(),
) {

    val todoState by todoStateProvider.toState()

    TodoEditView(
        todoItem = todoState.items.getOrNull(itemIndex),
        perform = { action -> actionHandler.handle(action) },
    )
}
