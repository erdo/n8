package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.features.fruit.FruitModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerFruitScreen(
    private val fruitModel: FruitModel = koinInject(),
) : GlobalActionHandler<Act.ScreenFruit>() {

    override fun __handle(act: Act.ScreenFruit) {

        Fore.i("_handle ScreenFruit Action: $act")

        when (act) {
            Act.ScreenFruit.Fetch -> fruitModel.refreshFruit()
            Act.ScreenFruit.FetchSimulateFail -> fruitModel.refreshFruitForceFail()
        }
    }
}
