package foo.bar.n8.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.early.fore.compose.observeAsState
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.HeightBasedDp
import co.early.fore.ui.size.LocalWindowSize
import co.early.fore.ui.size.WindowSize
import foo.bar.clean.domain.features.favourites.FavouritesModel
import foo.bar.clean.domain.features.favourites.FavouritesState
import foo.bar.clean.domain.features.favourites.Feature
import co.early.n8.Location
import co.early.n8.Location.SpaceLaunchLocations.SpaceDetailLocation
import co.early.n8.Location.SpaceLaunchLocations.SpaceLaunchLocation
import co.early.n8.Location.TodoLocations.TodoAddLocation
import co.early.n8.Location.TodoLocations.TodoEditLocation
import co.early.n8.Location.TodoLocations.TodoLocation
import foo.bar.clean.ui.actionhandlers.Act
import foo.bar.clean.ui.R
import foo.bar.n8.ui.common.components.elements.SH2
import foo.bar.n8.ui.screens.counter.CounterScreen
import foo.bar.n8.ui.screens.favourite.FavouritesScreen
import foo.bar.n8.ui.screens.fruit.FruitScreen
import foo.bar.n8.ui.screens.navigation.NavigationScreen
import foo.bar.n8.ui.screens.settings.SetColorScreen
import foo.bar.n8.ui.screens.settings.SettingsScreen
import foo.bar.n8.ui.screens.spacelaunch.SpaceLaunchDetailScreen
import foo.bar.n8.ui.screens.spacelaunch.SpaceLaunchScreen
import foo.bar.n8.ui.screens.ticket.TicketScreen
import foo.bar.n8.ui.screens.todo.TodoAddScreen
import foo.bar.n8.ui.screens.todo.TodoEditScreen
import foo.bar.n8.ui.screens.todo.TodoScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Top level UI container for the app
 */
@Composable
fun <T : Act> AppNavigation(
    currentLocation: Location,
    size: WindowSize = LocalWindowSize.current,
    title: String = appScreenTitle(currentLocation),
    mainContent: @Composable (PaddingValues) -> Unit = appScreenContent(currentLocation),
    startDrawerItems: List<NavigationItem> = appStartDrawerItems(currentLocation),
    actionItems: List<NavigationItem> = appActionItems(currentLocation),
    bottomBarItems: List<NavigationItem> = appBottomBarItems(currentLocation),
    userActionHandler: foo.bar.clean.ui.actionhandlers.ActionHandler<T>,
    //userActionHandler: ActionHandler<T> = ActionHandlers(currentLocation),
) {

    Fore.d("AppNavigation()")

    val startDrawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val headerImageSize = HeightBasedDp(
        xs = 70.dp,
        m = 130.dp,
        l = 200.dp
    )

    ModalNavigationDrawer(
        drawerState = startDrawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = DrawerDefaults.shape,
                drawerTonalElevation = DrawerDefaults.ModalDrawerElevation,
                drawerContainerColor = MaterialTheme.colorScheme.inverseSurface,
            ) {
                Spacer(modifier = Modifier.height(SH2(size)))
                Image(
                    modifier = Modifier
                        .size(headerImageSize(size))
                        .padding(
                            start = NavigationDrawerItemDefaults.ItemPadding.calculateStartPadding(
                                LocalLayoutDirection.current
                            )
                        ),
                    painter = painterResource(
                        id = R.drawable.ic_launcher
                    ),
                    contentDescription = stringResource(R.string.header_image)
                )
                Spacer(modifier = Modifier.height(SH2(size)))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                ) {
                    startDrawerItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = item.view,
                            label = { Text(item.localisedDescription) },
                            selected = !item.enabled,
                            onClick = {
                                scope.launch { startDrawerState.close() }
                                if (item.enabled) {
                                    userActionHandler.handle(item.action)
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.inversePrimary,
                                unselectedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                selectedIconColor = MaterialTheme.colorScheme.inverseOnSurface,
                                selectedTextColor = MaterialTheme.colorScheme.inverseOnSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.inverseOnSurface,
                                unselectedTextColor = MaterialTheme.colorScheme.inverseOnSurface,
                            )
                        )
                    }
                }
            }
        },
        gesturesEnabled = true,
        scrimColor = DrawerDefaults.scrimColor,
        content = {
            ContentScaffold(
                bottomBarItems = bottomBarItems,
                title = title,
                mainContent = mainContent,
                startDrawerOpenRequested = { scope.launch { startDrawerState.open() } },
                actionItems = actionItems,
                userActionHandler = userActionHandler
            )
        }
    )
}

@Composable
fun appScreenTitle(location: Location): String {

    Fore.d("appScreenTitle()")

    return stringResource(
        id = when (location) {
            Location.CounterLocation -> R.string.counter_model
            is Location.TodoLocations -> R.string.todo_model
            is Location.SpaceLaunchLocations -> R.string.spacelaunch_model
            Location.FavouritesLocation -> R.string.favourites_model
            is Location.SettingsLocations -> R.string.settings_model
            is Location.FruitLocation -> R.string.fruit_model
            Location.TicketLocation -> R.string.ticket_model
            Location.NavigationLocation -> R.string.navigation_model
        }
    )
}

@Composable
fun appScreenContent(location: Location) =
    @Composable { paddingValues: PaddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

            Fore.d("appScreenContent()")

            when (location) {
                Location.CounterLocation -> CounterScreen()
                is Location.TodoLocations -> {
                    when(location){
                        TodoLocation -> TodoScreen()
                        is TodoEditLocation -> TodoEditScreen(itemIndex = location.index)
                        TodoAddLocation -> TodoAddScreen()
                    }
                }
                is Location.SpaceLaunchLocations -> {
                    when(location){
                        is SpaceDetailLocation -> SpaceLaunchDetailScreen(launchId = location.id)
                        SpaceLaunchLocation -> SpaceLaunchScreen()
                    }
                }
                Location.FavouritesLocation -> FavouritesScreen()
                Location.TicketLocation -> TicketScreen()
                is Location.FruitLocation -> FruitScreen()
                is Location.SettingsLocations -> {
                    when(location){
                        is Location.SettingsLocations.SetColor -> SetColorScreen()
                        is Location.SettingsLocations.SettingsLocation -> SettingsScreen(color = location.color)
                    }
                }
                Location.NavigationLocation -> NavigationScreen()
            }
        }
    }

@Composable
fun appStartDrawerItems(
    currentLocation: Location,
): List<NavigationItem> {

    Fore.d("appStartDrawerItems()")

    return listOf(
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = stringResource(id = R.string.counter_model)
                )
            },
            localisedDescription = stringResource(id = R.string.counter_model),
            action = Act.Global.ToCounterScreen,
            enabled = currentLocation != Location.CounterLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Create,
                    contentDescription = stringResource(id = R.string.todo_model)
                )
            },
            localisedDescription = stringResource(id = R.string.todo_model),
            action = Act.Global.ToTodoScreen,
            enabled = currentLocation !is Location.TodoLocations,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Send,
                    contentDescription = stringResource(id = R.string.spacelaunch_model)
                )
            },
            localisedDescription = stringResource(id = R.string.spacelaunch_model),
            action = Act.Global.ToSpaceLaunchScreen,
            enabled = currentLocation !is Location.SpaceLaunchLocations,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(id = R.string.ticket_model)
                )
            },
            localisedDescription = stringResource(id = R.string.ticket_model),
            action = Act.Global.ToTicketScreen,
            enabled = currentLocation !is Location.TicketLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = stringResource(id = R.string.favourites_model)
                )
            },
            localisedDescription = stringResource(id = R.string.favourites_model),
            action = Act.Global.ToFavouriteScreen,
            enabled = currentLocation != Location.FavouritesLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Place,
                    contentDescription = stringResource(id = R.string.navigation_model)
                )
            },
            localisedDescription = stringResource(id = R.string.navigation_model),
            action = Act.Global.ToNavigationScreen,
            enabled = currentLocation !is Location.NavigationLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings_model)
                )
            },
            localisedDescription = stringResource(id = R.string.settings_model),
            action = Act.Global.ToSettingsScreen,
            enabled = currentLocation !is Location.SettingsLocations,
        ),
    )
}

@Composable
fun appActionItems(
    currentLocation: Location,
    favouritesModel: FavouritesModel = koinInject(),
): List<NavigationItem> {

    Fore.d("appActionItems()")

    val favouritesState by favouritesModel.observeAsState { favouritesModel.state }

    return when (currentLocation) {
        Location.CounterLocation -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Counter
                )
            )
        }

        is Location.TodoLocations -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Todo
                )
            )
        }

        is Location.SpaceLaunchLocations -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.SpaceLaunch
                )
            )
        }

        Location.FavouritesLocation -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Favourites
                )
            )
        }

        is Location.SettingsLocations -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Settings
                )
            )
        }

        is Location.FruitLocation -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Fruit
                )
            )
        }

        is Location.TicketLocation -> {
            listOf(
                favNavigationItem(
                    favouritesState = favouritesState,
                    feature = Feature.Ticket
                )
            )
        }

        else -> emptyList()
    }
}

@Composable
fun appBottomBarItems(
    currentLocation: Location,
): List<NavigationItem>{

    Fore.d("appBottomBarItems()")

    return listOf(
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = stringResource(id = R.string.counter_model)
                )
            },
            localisedDescription = stringResource(id = R.string.counter_model),
            action = Act.Global.ToCounterScreen,
            enabled = currentLocation != Location.CounterLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = stringResource(id = R.string.favourites_model)
                )
            },
            localisedDescription = stringResource(id = R.string.favourites_model),
            action = Act.Global.ToFavouriteScreen,
            enabled = currentLocation != Location.FavouritesLocation,
        ),
        NavigationItem(
            view = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings_model)
                )
            },
            localisedDescription = stringResource(id = R.string.settings_model),
            action = Act.Global.ToSettingsScreen,
            enabled = currentLocation !is Location.SettingsLocations,
        ),
    )
}

// TODO why does this not work? (see AppNavigation function above)
//@Composable
//fun <T : Act> ActionHandlers(
//    currentLocation: Location,
//): ActionHandler<T> {
//
//    Fore.i("ActionHandlers()")
//
//    return when (currentLocation) {
//        Location.CounterLocation -> (koinInject() as ActionHandlerCounterScreen)
//        is Location.TodoLocations -> (koinInject() as ActionHandlerTodoScreen)
//        is Location.SpaceLaunchLocations -> (koinInject() as ActionHandlerSpaceLaunchScreen)
//        Location.FavouriteLocation -> (koinInject() as ActionHandlerFavouritesScreen)
//        is Location.SettingsLocations -> (koinInject() as ActionHandlerSettingsScreen)
//        is Location.FruitLocation -> (koinInject() as ActionHandlerFruitScreen)
//        is Location.TicketLocation -> (koinInject() as ActionHandlerTicketScreen)
//        Location.NavigationLocation -> (koinInject() as ActionHandlerNavigationScreen)
//    }
//}




data class NavigationItem(
    val view: @Composable () -> Unit,
    val localisedDescription: String,
    val action: Act,
    val enabled: Boolean = true,
    val checked: Boolean = false,
    val type: NavigationItemType = NavigationItemType.ICON
)

enum class NavigationItemType {
    ICON,
    ICON_TOGGLE,
    SWITCH,
}

@Composable
fun favNavigationItem(
    favouritesState: FavouritesState,
    feature: Feature,
): NavigationItem {
    return NavigationItem(
        view = {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(id = R.string.favourites)
            )
        },
        localisedDescription = stringResource(id = R.string.favourites),
        action = foo.bar.clean.ui.actionhandlers.Act.Global.ToggleFavourite(feature = feature),
        enabled = true,
        checked = favouritesState.isFavourite(feature),
        type = NavigationItemType.ICON_TOGGLE
    )
}
