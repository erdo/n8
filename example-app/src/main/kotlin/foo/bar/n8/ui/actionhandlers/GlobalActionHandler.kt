package foo.bar.n8.ui.actionhandlers

import co.early.fore.kt.core.delegate.Fore
import foo.bar.clean.domain.features.favourites.FavouritesModel
import co.early.n8.Location
import co.early.n8.NavigationModel
import foo.bar.clean.domain.services.api.Fruit
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatformTools

abstract class GlobalActionHandler<T : Act>(
    private val favouritesModel: FavouritesModel = koinInject(),
    private val navModel: NavigationModel = koinInject(),
) : ActionHandler<T>() {

    override fun _handle(act: Act.Global) {

        Fore.i("_handle Global Action: $act")

        when (act) {
            Act.Global.Back -> navModel.popBackStack()
            Act.Global.ToCounterScreen -> navModel.navigateBackTo(Location.CounterLocation)
            Act.Global.ToTicketScreen -> navModel.navigateBackTo(Location.TicketLocation)
            is Act.Global.ToFruitScreen -> navModel.navigateBackTo(
                Location.FruitLocation(overrideFruit = Fruit.FruitNone)
            )
            Act.Global.ToFavouriteScreen -> navModel.navigateBackTo(Location.FavouritesLocation)
            Act.Global.ToSettingsScreen -> navModel.navigateBackTo(Location.SettingsLocations.SettingsLocation())
            is Act.Global.ToggleFavourite -> favouritesModel.toggleFavourite(act.feature)
            Act.Global.ToSpaceLaunchScreen -> navModel.navigateBackTo(Location.SpaceLaunchLocations.SpaceLaunchLocation)
            Act.Global.ToTodoScreen -> navModel.navigateBackTo(Location.TodoLocations.TodoLocation)
            Act.Global.ToNavigationScreen -> navModel.navigateBackTo(Location.NavigationLocation)
        }
    }
}

/**
 * lets us access koin's inject() function from a default parameter in a non compose context
 */
inline fun <reified T : Any> koinInject(
    qualifier: Qualifier? = null,
    mode: LazyThreadSafetyMode = KoinPlatformTools.defaultLazyMode(),
    noinline parameters: ParametersDefinition? = null,
): T = KoinPlatformTools.defaultContext().get().inject<T>().value
