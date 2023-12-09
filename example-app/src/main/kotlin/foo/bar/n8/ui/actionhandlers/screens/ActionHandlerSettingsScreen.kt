package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import co.early.n8.Location
import co.early.n8.NavigationModel
import foo.bar.clean.domain.features.settings.SettingsModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerSettingsScreen(
    private val settingsModel: SettingsModel = koinInject(),
    private val navModel: NavigationModel = koinInject(),
) : GlobalActionHandler<Act.ScreenSettings>() {

    override fun __handle(act: Act.ScreenSettings) {

        Fore.i("_handle ScreenSettings Action: $act")

        when (act) {
            is Act.ScreenSettings.SetDarkMode -> settingsModel.setDarkMode(act.darkMode)
            is Act.ScreenSettings.SetColorAndBack -> navModel.popBackStack {
                when (it) {
                    is Location.SettingsLocations.SettingsLocation -> {
                        it.copy(color = act.color)
                    }
                    else -> it
                }
            }
            Act.ScreenSettings.ToSetColorScreen -> navModel.navigateTo(
                Location.SettingsLocations.SetColor
            )
        }
    }
}
