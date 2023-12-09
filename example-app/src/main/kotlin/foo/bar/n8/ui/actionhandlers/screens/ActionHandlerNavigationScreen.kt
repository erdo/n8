package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.NavigationModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerNavigationScreen(
    private val navModel: NavigationModel = koinInject(),
) : GlobalActionHandler<Act.ScreenNavigation>() {

    override fun __handle(act: Act.ScreenNavigation) {

        Fore.i("_handle ScreenNavigation Action: $act")

        when (act) {
            is Act.ScreenNavigation.UpdateBackstack -> {
                navModel.updateBackStack(newBackStack = act.backstack)
            }
        }
    }
}
