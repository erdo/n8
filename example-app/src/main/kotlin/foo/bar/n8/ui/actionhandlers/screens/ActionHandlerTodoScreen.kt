package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.Location.TodoLocations.TodoAddLocation
import co.early.n8.Location.TodoLocations.TodoEditLocation
import co.early.n8.NavigationModel
import foo.bar.clean.domain.features.todo.TodoModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerTodoScreen(
    private val todoModel: TodoModel = koinInject(),
    private val navModel: NavigationModel = koinInject(),
) : GlobalActionHandler<Act.ScreenTodo>() {

    override fun __handle(act: Act.ScreenTodo) {

        Fore.i("_handle ScreenTodo Action: $act busy:${todoModel.state.loading}")

        if (todoModel.state.loading) {
            return
        }

        when (act) {
            is Act.ScreenTodo.ToEditScreen -> navModel.navigateTo(TodoEditLocation(act.index))
            is Act.ScreenTodo.ToggleDone -> todoModel.toggleDoneForItem(act.index)
            Act.ScreenTodo.ToggleShowDone -> todoModel.toggleShowDone()
            is Act.ScreenTodo.ItemDelete -> todoModel.deleteItem(act.index)
            Act.ScreenTodo.ToAddScreen -> navModel.navigateTo(TodoAddLocation)
            is Act.ScreenTodo.UpdateThenBack -> {
                todoModel.updateItem(act.item)
                navModel.popBackStack()
            }
            is Act.ScreenTodo.CreateThenBack -> {
                todoModel.createItem(act.label)
                navModel.popBackStack()
            }
        }
    }
}
