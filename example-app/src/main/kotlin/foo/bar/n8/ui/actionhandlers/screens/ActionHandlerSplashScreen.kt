package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.features.init.InitModel
import foo.bar.clean.domain.features.init.PreInitModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.ActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

/**
 * The SplashScreen Action Handler is unique in that it doesn't extend the global action handler,
 * the only valid actions from the SplashScreen are retry. Back will be handled by the system as
 * at this point the navigation graph has not yet been instantiated
 */
class ActionHandlerSplashScreen(
    private val initModel: InitModel = koinInject(),
    private val preInitModel: PreInitModel = koinInject(),
) : ActionHandler<Act.ScreenSplash>() {

    override fun __handle(act: Act.ScreenSplash) {

        Fore.i("_handle ScreenSplash Action: $act")

        when (act) {
            Act.ScreenSplash.Retry -> initModel.retry()
            Act.ScreenSplash.ClearUpgradeNag -> preInitModel.acknowledgeNag()
        }
    }

    override fun _handle(act: Act.Global) {
        throw RuntimeException(
            "The splash screen doesn't handle any global navigation actions," +
                    "please check your code"
        )
    }
}
