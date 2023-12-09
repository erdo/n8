package foo.bar.n8.ui.actionhandlers

import foo.bar.clean.domain.features.favourites.Feature
import co.early.n8.Location
import foo.bar.clean.domain.features.settings.DarkMode
import foo.bar.clean.domain.services.api.Fruit
import foo.bar.clean.domain.services.db.TodoItem

/**
 * All the actions that a user can take from the UI, organised by screen
 */

sealed class Act {

    sealed class Global : Act() {
        data object Back : Global()
        data class ToggleFavourite(val feature: Feature) : Global()
        data object ToCounterScreen : Global()
        data object ToTodoScreen : Global()
        data object ToFavouriteScreen : Global()
        data class ToFruitScreen(val setFruit: Fruit = Fruit.FruitNone) : Global()
        data object ToTicketScreen : Global()
        data object ToNavigationScreen : Global()
        data object ToSpaceLaunchScreen : Global()
        data object ToSettingsScreen : Global()
    }

    sealed class ScreenSplash : Act() {
        data object Retry : ScreenSplash()
        data object ClearUpgradeNag : ScreenSplash()
    }

    sealed class ScreenCounter : Act() {
        data object IncreaseCounter : ScreenCounter()
        data object DecreaseCounter : ScreenCounter()
    }

    sealed class ScreenTodo : Act() {
        data object ToggleShowDone : ScreenTodo()
        data class ToggleDone(val index: Int) : ScreenTodo()
        data object ToAddScreen : ScreenTodo()
        data class ToEditScreen(val index: Int) : ScreenTodo()
        data class ItemDelete(val index: Int) : ScreenTodo()
        data class UpdateThenBack(val item: TodoItem) : ScreenTodo()
        data class CreateThenBack(val label: String) : ScreenTodo()
    }

    sealed class ScreenFavourites : Act() {
        object ClearAllFavourites : ScreenFavourites()
        data class ClearFavourite(val feature: Feature) : ScreenFavourites()
    }

    sealed class ScreenSpaceLaunch : Act() {
        data object RefreshLaunches : ScreenSpaceLaunch()
        data class SelectLaunch(val id: String) : ScreenSpaceLaunch()
        data class RefreshLaunchDetail(val id: String) : ScreenSpaceLaunch()
        data object MakeBooking : ScreenSpaceLaunch()
        data object CancelBooking : ScreenSpaceLaunch()
        data object ClearErrors : ScreenSpaceLaunch()
        data object SignIn : ScreenSpaceLaunch()
        data object SignOut : ScreenSpaceLaunch()
        data object ClearData : ScreenSpaceLaunch()
    }

    sealed class ScreenTicket : Act() {
        data object RequestTicket : ScreenTicket()
        data object ClearData : ScreenTicket()
        data object ClearError : ScreenTicket()
    }

    sealed class ScreenSettings : Act() {
        data class SetDarkMode(val darkMode: DarkMode) : ScreenSettings()
        data object ToSetColorScreen : ScreenSettings()
        data class SetColorAndBack(val color: ULong?) : ScreenSettings()
    }

    sealed class ScreenFruit : Act() {
        data object Fetch : ScreenFruit()
        data object FetchSimulateFail : ScreenFruit()
    }

    sealed class ScreenNavigation : Act() {
        data class UpdateBackstack(val backstack: List<Location>) : ScreenNavigation()
    }

    sealed class ScreenNetwork : Act()

    sealed class ScreenWeather : Act() {
        data object RefreshWeather : ScreenWeather()
        data object ToggleAutoUpdate : ScreenWeather()
    }

    sealed class ScreenColourSelector : Act() {
        data class ChooseColour(val colourInt: Int) : ScreenColourSelector()
    }
}
