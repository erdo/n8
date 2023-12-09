package foo.bar.n8.ui.actionhandlers.screens

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.features.auth.AuthModel
import co.early.n8.Location
import co.early.n8.NavigationModel
import foo.bar.clean.domain.features.spacelaunch.SpaceDetailModel
import foo.bar.clean.domain.features.spacelaunch.SpaceLaunchModel
import foo.bar.n8.ui.actionhandlers.Act
import foo.bar.n8.ui.actionhandlers.GlobalActionHandler
import foo.bar.n8.ui.actionhandlers.koinInject

class ActionHandlerSpaceLaunchScreen(
    private val spaceLaunchModel: SpaceLaunchModel = koinInject(),
    private val spaceDetailModel: SpaceDetailModel = koinInject(),
    private val authModel: AuthModel = koinInject(),
    private val navModel: NavigationModel = koinInject(),
) : GlobalActionHandler<Act.ScreenSpaceLaunch>() {

    override fun __handle(act: Act.ScreenSpaceLaunch) {

        Fore.i("_handle ScreenSpaceLaunch Action: $act")

        when (act) {
            is Act.ScreenSpaceLaunch.CancelBooking -> spaceDetailModel.cancelLaunch()
            is Act.ScreenSpaceLaunch.MakeBooking -> spaceDetailModel.bookLaunch()
            is Act.ScreenSpaceLaunch.SelectLaunch -> {
                spaceDetailModel.clearData()
                navModel.navigateTo(Location.SpaceLaunchLocations.SpaceDetailLocation(act.id))
            }
            Act.ScreenSpaceLaunch.RefreshLaunches -> spaceLaunchModel.refreshLaunchList()
            Act.ScreenSpaceLaunch.ClearErrors -> {
                spaceLaunchModel.clearError()
                spaceDetailModel.clearError()
            }
            Act.ScreenSpaceLaunch.SignIn -> authModel.signIn()
            Act.ScreenSpaceLaunch.SignOut -> authModel.signOut()
            Act.ScreenSpaceLaunch.ClearData -> spaceLaunchModel.clearData()
            is Act.ScreenSpaceLaunch.RefreshLaunchDetail -> {
                spaceDetailModel.setLaunch(act.id)
                spaceDetailModel.fetchLaunchDetail()
            }
        }
    }
}
