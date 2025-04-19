package foo.bar.n8.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import backInterceptor
import co.early.fore.core.delegate.Fore
import co.early.fore.ui.size.WindowSize
import co.early.n8.N8
import co.early.n8.NavigationModel
import co.early.n8.compose.N8Host
import co.early.n8.isIndexOnPath
import foo.bar.n8.ui.common.StateWrapperView
import foo.bar.n8.ui.navigation.Location
import foo.bar.n8.ui.navigation.TabHostId
import foo.bar.n8.ui.navigation.tabHostSpecMain
import foo.bar.n8.ui.screens.BangkokScreen
import foo.bar.n8.ui.screens.DakarScreen
import foo.bar.n8.ui.screens.DamascusScreen
import foo.bar.n8.ui.screens.HomeScreen
import foo.bar.n8.ui.screens.HoustonScreen
import foo.bar.n8.ui.screens.KrakowScreen
import foo.bar.n8.ui.screens.LaScreen
import foo.bar.n8.ui.screens.LagosScreen
import foo.bar.n8.ui.screens.LondonScreen
import foo.bar.n8.ui.screens.MilanScreen
import foo.bar.n8.ui.screens.MumbaiScreen
import foo.bar.n8.ui.screens.NewYorkScreen
import foo.bar.n8.ui.screens.ParisScreen
import foo.bar.n8.ui.screens.SeoulScreen
import foo.bar.n8.ui.screens.ShanghaiScreen
import foo.bar.n8.ui.screens.StockholmScreen
import foo.bar.n8.ui.screens.SydneyScreen
import foo.bar.n8.ui.screens.TokyoScreen

@OptIn(ExperimentalMaterial3Api::class)
class Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fore.i("onCreate()")

        setContent {

            WindowSize {

                N8Host(onBack = backInterceptor() ) { navigationState ->

                    val location = navigationState.currentLocation
                    Fore.i("Latest Location is:$location")
                    Fore.i("Previous Location was:${navigationState.comingFrom}")

                    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

                    // access N8 via the delegate or pass the instance around using DI
                    val n8: NavigationModel<Location, TabHostId> = N8.n8()

                    // from within the N8Host{} scope you can also access LocalN8HostState
                    // val navigationState = LocalN8HostState

                    if (navigationState.initialLoading){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {

                        Scaffold(
                            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                            topBar = {},
                            content = { scaffoldPadding ->
                                val paddingValues = PaddingValues(
                                    end = scaffoldPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp, // https://stackoverflow.com/a/76029778
                                    start = scaffoldPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                                    top = scaffoldPadding.calculateTopPadding() + 8.dp,
                                    bottom = scaffoldPadding.calculateBottomPadding()
                                )
                                MainContent(location, paddingValues)
                            },
                            bottomBar = {
                                if (location != Location.Home) {
                                    NavigationBar {
                                        TabUi(
                                            text = "Tab 0",
                                            image = Default.AddCircle,
                                            enabled = !navigationState.hostedBy.isIndexOnPath(
                                                index = 0,
                                                tabHostId = tabHostSpecMain.tabHostId
                                            )
                                        ) {
                                            n8.switchTab(tabHostSpecMain, 0)
                                        }
                                        TabUi(
                                            text = "Tab 1",
                                            image = Default.Favorite,
                                            enabled = !navigationState.hostedBy.isIndexOnPath(
                                                index = 1,
                                                tabHostId = tabHostSpecMain.tabHostId
                                            )
                                        ) {
                                            n8.switchTab(tabHostSpecMain, 1)
                                        }
                                        TabUi(
                                            text = "Tab 2",
                                            image = Default.Settings,
                                            enabled = !navigationState.hostedBy.isIndexOnPath(
                                                index = 2,
                                                tabHostId = tabHostSpecMain.tabHostId
                                            )
                                        ) {
                                            n8.switchTab(tabHostSpecMain, 2)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabUi(text: String, image: ImageVector, enabled: Boolean, action: () -> Unit) {
    NavigationBarItem(
        icon = {
            Icon(
                image,
                contentDescription = text
            )
        },
        label = {
            Text(
                text = text,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                maxLines = 1,
            )
        },
        selected = enabled,
        onClick = {
            if (enabled) {
                action()
            }
        }
    )
}

@Composable
fun MainContent(
    location: Location,
    paddingValues: PaddingValues,
    n8: NavigationModel<Location, TabHostId> = N8.n8()
) {

    Box(modifier = Modifier.padding(paddingValues)) {

        Fore.d("MainContent() location:$location")

        StateWrapperView(
            stateAsString = n8.toString(diagnostics = false),
        ) {
            when (location) {
                Location.Home -> HomeScreen()
                Location.Bangkok -> BangkokScreen()
                Location.Dakar -> DakarScreen()
                Location.Damascus -> DamascusScreen()
                Location.Houston -> HoustonScreen()
                Location.LA -> LaScreen()
                Location.Lagos -> LagosScreen()
                Location.Mumbai -> MumbaiScreen()
                Location.NewYork -> NewYorkScreen()
                Location.Seoul -> SeoulScreen()
                Location.Shanghai -> ShanghaiScreen()
                Location.Sydney -> SydneyScreen()
                Location.Tokyo -> TokyoScreen()
                is Location.EuropeanLocations -> {
                    when (location) {
                        Location.EuropeanLocations.London -> LondonScreen()
                        Location.EuropeanLocations.Milan -> MilanScreen()
                        Location.EuropeanLocations.Paris -> ParisScreen()
                        Location.EuropeanLocations.Stockholm -> StockholmScreen()
                        Location.EuropeanLocations.Krakow -> KrakowScreen()
                    }
                }
            }
        }
    }
}
