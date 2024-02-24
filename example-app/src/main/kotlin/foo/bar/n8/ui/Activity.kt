package foo.bar.n8.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.early.fore.kt.core.delegate.Fore
import co.early.fore.ui.size.WindowSize
import co.early.n8.NavigationModel
import co.early.n8.compose.N8
import co.early.n8.compose.N8Host
import co.early.n8.isSelected
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

                // TODO would like to get rid of having to specify the generics if possible
                N8Host<Location, TabHostId> { navigationState ->

                    val location = navigationState.currentLocation
                    Fore.i("Location is:$location")

                    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                    val n8: NavigationModel<Location, TabHostId> = N8.n8()

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
                                        enabled = !n8.state.hostedBy.isSelected(
                                            tabHostId = tabHostSpecMain.tabHostId,
                                            index = 0
                                        )
                                    ) {
                                        n8.switchTab(tabHostSpecMain, 0)
                                    }
                                    TabUi(
                                        text = "Tab 1",
                                        image = Default.Favorite,
                                        enabled = !n8.state.hostedBy.isSelected(
                                            tabHostId = tabHostSpecMain.tabHostId,
                                            index = 1
                                        )
                                    ) {
                                        n8.switchTab(tabHostSpecMain, 1)
                                    }
                                    TabUi(
                                        text = "Tab 2",
                                        image = Default.Settings,
                                        enabled = !n8.state.hostedBy.isSelected(
                                            tabHostId = tabHostSpecMain.tabHostId,
                                            index = 2
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
private fun TitleContent(titleText: String) = Text(
    modifier = Modifier,
    text = titleText,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)

@Composable
fun MainContent(location: Location, paddingValues: PaddingValues) {

    Box(modifier = Modifier.padding(paddingValues)) {

        Fore.d("appScreenContent()")

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
