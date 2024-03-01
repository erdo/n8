package foo.bar.n8.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.WindowSize
import co.early.n8.Location
import co.early.n8.Location.*
import co.early.n8.Navigation
import co.early.n8.Navigation.EndNode
import co.early.n8.backStackOf
import co.early.n8.endNodeOf
import co.early.n8.tabsOf
import foo.bar.n8.ui.common.splash.SplashScreen
import foo.bar.n8.ui.navigation.appBottomBarItems
import foo.bar.n8.ui.navigation.AppNavigation
import foo.bar.n8.ui.navigation.NavHost
import foo.bar.n8.ui.theme.AppTheme
import org.koin.compose.koinInject


fun test1() {
    val nav = backStackOf(
        endNodeOf(A),
        endNodeOf(B),
        tabsOf(
            selectedTabHistory = listOf(0),
            backStackOf(
                endNodeOf(TabX.X1),
                endNodeOf(C),
                endNodeOf(D),
                tabsOf(
                    selectedTabHistory = listOf(0, 1),
                    backStackOf(
                        endNodeOf(TabY.Y1),
                        endNodeOf(E),
                    ),
                    backStackOf(
                        endNodeOf(TabY.Y2),
                    )
                )
            ),
            backStackOf(
                endNodeOf(TabX.X1),
            ),
            backStackOf(
                endNodeOf(TabX.X2),
            ),
        )
    )
}

class Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        Fore.i("onCreate()")
get a super basic example app running with emtpy screens
        setContent {
            AppTheme {
                WindowSize {
                    SplashScreen {
                        NavHost { navigationState ->
                            val location = navigationState.currentPage()

                            Fore.i("Location is:$location")

                            AppNavigation(
                                currentLocation = location,
                                userActionHandler = when (location) {
                                    Location.CounterLocation -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerCounterScreen)
                                    is Location.TodoLocations -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerTodoScreen)
                                    is Location.SpaceLaunchLocations -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSpaceLaunchScreen)
                                    Location.FavouritesLocation -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerFavouritesScreen)
                                    is Location.SettingsLocations -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerSettingsScreen)
                                    is Location.FruitLocation -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerFruitScreen)
                                    is Location.TicketLocation -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerTicketScreen)
                                    Location.NavigationLocation -> (koinInject() as foo.bar.clean.ui.actionhandlers.screens.ActionHandlerNavigationScreen)
                                },
                                bottomBarItems = when (location) {
                                    is Location.TicketLocation -> emptyList()
                                    else -> appBottomBarItems(location)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
